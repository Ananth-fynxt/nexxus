package fynxt.brand.transaction.step.impl;

import fynxt.brand.psp.service.PspService;
import fynxt.brand.request.entity.RequestPspId;
import fynxt.brand.request.repository.RequestPspRepository;
import fynxt.brand.request.repository.RequestRepository;
import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.brand.transaction.step.AbstractTransactionStep;
import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.ErrorCategory;
import fynxt.common.exception.TransactionException;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class TransactionCreationStep extends AbstractTransactionStep {

	private final PspService pspService;
	private final RequestRepository requestRepository;
	private final RequestPspRepository requestPspRepository;

	public TransactionCreationStep(
			PspService pspService,
			RequestRepository requestRepository,
			RequestPspRepository requestPspRepository,
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {
		super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
		this.pspService = pspService;
		this.requestRepository = requestRepository;
		this.requestPspRepository = requestPspRepository;
	}

	@Override
	@Transactional
	public TransactionExecutionContext execute(TransactionExecutionContext context) {
		if (!precondition(context)) {
			Transaction tx = context.getTransaction();
			throw new TransactionException(
					String.format(
							"Precondition failed for transition %s -> %s (flowTargetId=%s, flowActionId=%s, txnId=%s)",
							tx.getStatus(),
							getDestinationStatus(),
							tx.getFlowTargetId(),
							tx.getFlowActionId(),
							context.getTxnId()),
					ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
		}
		context = doExecute(context);
		Transaction newTransaction =
				transactionMapper.createNewVersionedRecord(context.getTransaction(), getDestinationStatus());
		Transaction savedTransaction = transactionRepository.save(newTransaction);
		return context.toBuilder().transaction(savedTransaction).build();
	}

	@Override
	protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
		performCreateValidations(context);
		TransactionDto transactionDto = transactionMapper.toDto(context.getTransaction());
		if (transactionDto.getPspId() != null) {
			pspService.getPspIfEnabled(transactionDto.getPspId());
		}
		return context;
	}

	private void performCreateValidations(TransactionExecutionContext context) {
		Transaction transaction = context.getTransaction();
		validateExternalRequestIdPresence(transaction);
		validateDuplicateTransaction(transaction);
		validateRequestIdNotAlreadyMapped(transaction);
		validateTxnAmountMatchesRequestAmount(transaction);
		validateRequestIdAndPspIdMapping(transaction);
		validateFlowDefinitionHasScript(transaction);
	}

	private void validateExternalRequestIdPresence(Transaction transaction) {
		if (null == transaction) {
			throw new TransactionException(
					"Transaction is null in execution context", ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
		}

		if (!StringUtils.hasText(transaction.getExternalRequestId())) {
			throw new TransactionException(
					"Transaction external request Id is required",
					ErrorCode.INVALID_REQUEST_BODY,
					ErrorCategory.BAD_REQUEST);
		}
	}

	private void validateDuplicateTransaction(Transaction transaction) {
		Transaction existingTransaction = transactionRepository.findLatestByExternalRequestIdForContext(
				transaction.getBrandId(),
				transaction.getEnvironmentId(),
				transaction.getFlowActionId(),
				transaction.getExternalRequestId());

		if (null == existingTransaction) {
			return;
		}

		throw new TransactionException(
				String.format(
						"Transaction with externalRequestId '%s' already exists for this brand/environment/flowAction. Existing transaction ID: %s",
						transaction.getExternalRequestId(),
						existingTransaction.getId() != null
								? existingTransaction.getId().getTxnId()
								: "UNKNOWN"),
				ErrorCode.TRANSACTION_DUPLICATE,
				ErrorCategory.DUPLICATE);
	}

	private void validateRequestIdNotAlreadyMapped(Transaction transaction) {
		if (transaction.getRequestId() == null) {
			throw new TransactionException(
					"requestId is required to create a transaction",
					ErrorCode.TRANSACTION_REQUEST_ID_NOT_FOUND,
					ErrorCategory.BAD_REQUEST);
		}

		Transaction existingTransaction = transactionRepository.findLatestByRequestIdForContext(
				transaction.getBrandId(), transaction.getEnvironmentId(), transaction.getRequestId());
		if (existingTransaction == null) {
			return;
		}

		throw new TransactionException(
				String.format(
						"requestId '%s' is already mapped to an existing transaction '%s'. Please provide a different requestId to create a new transaction.",
						transaction.getRequestId(),
						existingTransaction.getId() != null
								? existingTransaction.getId().getTxnId()
								: "UNKNOWN"),
				ErrorCode.DUPLICATE_RESOURCE,
				ErrorCategory.DUPLICATE);
	}

	private void validateTxnAmountMatchesRequestAmount(Transaction transaction) {
		var request = requestRepository
				.findById(transaction.getRequestId())
				.orElseThrow(() -> new TransactionException(
						"No request found for provided requestId",
						ErrorCode.RESOURCE_NOT_FOUND,
						ErrorCategory.NOT_FOUND));

		// Tenant safety: if request exists but belongs to another brand/env, treat as not found.
		if (!request.getBrandId().equals(transaction.getBrandId())
				|| !request.getEnvironmentId().equals(transaction.getEnvironmentId())) {
			throw new TransactionException(
					"No request found for provided requestId", ErrorCode.RESOURCE_NOT_FOUND, ErrorCategory.NOT_FOUND);
		}

		if (request.getAmount() == null || transaction.getTxnAmount() == null) {
			throw new TransactionException(
					"Both request.amount and transaction.txnAmount must be provided",
					ErrorCode.TRANSACTION_AMOUNT_REQUIRED,
					ErrorCategory.BAD_REQUEST);
		}

		if (request.getAmount().compareTo(transaction.getTxnAmount()) != 0) {
			throw new TransactionException(
					String.format(
							"Amount mismatch: request.amount=%s but transaction.txnAmount=%s",
							request.getAmount(), transaction.getTxnAmount()),
					ErrorCode.TRANSACTION_AMOUNT_INVALID,
					ErrorCategory.BAD_REQUEST);
		}

		if (!StringUtils.hasText(request.getFlowActionId()) || !StringUtils.hasText(transaction.getFlowActionId())) {
			throw new TransactionException(
					"Both request.actionId and transaction.flowActionId must be provided",
					ErrorCode.TRANSACTION_FLOW_ACTION_ID_REQUIRED,
					ErrorCategory.BAD_REQUEST);
		}

		if (!request.getFlowActionId().equals(transaction.getFlowActionId())) {
			throw new TransactionException(
					String.format(
							"ActionId mismatch: request.actionId=%s but transaction.flowActionId=%s",
							request.getFlowActionId(), transaction.getFlowActionId()),
					ErrorCode.TRANSACTION_VALIDATION_FAILED,
					ErrorCategory.BAD_REQUEST);
		}

		if (!StringUtils.hasText(request.getCurrency()) || !StringUtils.hasText(transaction.getTxnCurrency())) {
			throw new TransactionException(
					"Both request.currency and transaction.txnCurrency must be provided",
					ErrorCode.TRANSACTION_CURRENCY_REQUIRED,
					ErrorCategory.BAD_REQUEST);
		}

		if (!request.getCurrency().equalsIgnoreCase(transaction.getTxnCurrency())) {
			throw new TransactionException(
					String.format(
							"Currency mismatch: request.currency=%s but transaction.txnCurrency=%s",
							request.getCurrency(), transaction.getTxnCurrency()),
					ErrorCode.TRANSACTION_CURRENCY_INVALID,
					ErrorCategory.BAD_REQUEST);
		}
	}

	private void validateRequestIdAndPspIdMapping(Transaction transaction) {
		if (transaction.getPspId() == null) {
			return;
		}

		boolean exists =
				requestPspRepository.existsById(new RequestPspId(transaction.getRequestId(), transaction.getPspId()));
		if (exists) {
			return;
		}

		throw new TransactionException(
				String.format(
						"pspId '%s' is not associated with requestId '%s' (request_psps mapping not found).",
						transaction.getPspId(), transaction.getRequestId()),
				ErrorCode.VALIDATION_ERROR,
				ErrorCategory.BAD_REQUEST);
	}

	private void validateFlowDefinitionHasScript(Transaction transaction) {
		var flowDefinitionOpt = transactionFlowConfigurationService.getFlowDefinition(
				transaction.getFlowTargetId(), transaction.getFlowActionId());

		if (flowDefinitionOpt.isEmpty()) {
			throw new TransactionException(
					String.format(
							"No flow definition found for flowTargetId '%s' and flowActionId '%s'",
							transaction.getFlowTargetId(), transaction.getFlowActionId()),
					ErrorCode.FLOW_DEFINITION_NOT_FOUND,
					ErrorCategory.NOT_FOUND);
		}

		var flowDefinition = flowDefinitionOpt.get();
		if (!StringUtils.hasText(flowDefinition.getCode())) {
			throw new TransactionException(
					String.format(
							"Flow definition for flowTargetId '%s' and flowActionId '%s' has no script/code configured",
							transaction.getFlowTargetId(), transaction.getFlowActionId()),
					ErrorCode.FLOW_DEFINITION_CODE_REQUIRED,
					ErrorCategory.BAD_REQUEST);
		}
	}

	@Override
	public TransactionStatus getDestinationStatus() {
		return TransactionStatus.CREATED;
	}
}
