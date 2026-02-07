package fynxt.brand.transaction.orchestrator.impl;

import fynxt.brand.request.service.RequestService;
import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.orchestrator.TransactionOrchestrator;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.brand.transaction.step.TransactionStep;
import fynxt.brand.transaction.step.factory.TransactionStepFactory;
import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.AppException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionOrchestratorImpl implements TransactionOrchestrator {

	@Autowired
	private TransactionStepFactory stepFactory;

	@Autowired
	private TransactionFlowConfigurationService flowConfigurationService;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private RequestService requestService;

	public TransactionExecutionContext createTransaction(TransactionDto transactionDto) {
		Transaction transaction = transactionMapper.toEntity(transactionDto);
		transaction.setStatus(TransactionStatus.NEW);
		populateCustomerFields(transaction);
		TransactionExecutionContext context = TransactionExecutionContext.builder()
				.transaction(transaction)
				.isFirstExecution(true)
				.build();
		return executeNextStep(context);
	}

	private void populateCustomerFields(Transaction transaction) {
		UUID requestId = transaction.getRequestId();
		if (requestId != null) {
			try {
				RequestService.CustomerInfo customerInfo = requestService.getCustomerInfoByRequestId(requestId);
				transaction.setCustomerId(customerInfo.customerId());
				transaction.setCustomerTag(customerInfo.customerTag());
				transaction.setCustomerAccountType(customerInfo.customerAccountType());
			} catch (Exception e) {
			}
		}
	}

	public TransactionExecutionContext executeNextStep(TransactionExecutionContext context) {
		try {
			TransactionStep nextStep = determineNextStep(context);
			return executeStep(context, nextStep);
		} catch (AppException e) {
			Transaction transaction = context.getTransaction();
			if (transaction != null
					&& transaction.getId() != null
					&& transaction.getId().getTxnId() != null
					&& !transaction.getStatus().equals(TransactionStatus.NEW)) {
				return context;
			}
			throw e;
		}
	}

	public TransactionExecutionContext transitionToStatus(
			TransactionExecutionContext context, TransactionStatus targetStatus) {
		verifyTransition(context.getTransaction(), targetStatus);
		TransactionStep targetStep = getTransactionStep(targetStatus);
		return executeStep(context, targetStep);
	}

	private TransactionStep getTransactionStep(TransactionStatus targetStatus) {
		TransactionStep targetStep = stepFactory.getStepForStatus(targetStatus);
		if (targetStep == null) {
			throw new AppException(
					"No transaction step found for status: " + targetStatus, ErrorCode.TRANSACTION_PROCESSING_ERROR);
		}
		return targetStep;
	}

	private TransactionStep determineNextStep(TransactionExecutionContext context) {
		List<TransactionStep> validSteps = getValidNextSteps(context);
		if (validSteps.isEmpty()) {
			throw new AppException("No valid transaction steps found", ErrorCode.TRANSACTION_NO_VALID_STEPS_FOUND);
		}
		if (validSteps.size() > 1) {
			throw new AppException(
					"Multiple valid transaction steps found", ErrorCode.TRANSACTION_MULTIPLE_STEPS_FOUND);
		}
		return validSteps.getFirst();
	}

	private List<TransactionStep> getValidNextSteps(TransactionExecutionContext context) {
		List<TransactionStatus> possibleNextStatuses = getPossibleNextStatuses(context.getTransaction());
		return findValidSteps(possibleNextStatuses, context);
	}

	private List<TransactionStatus> getPossibleNextStatuses(Transaction transaction) {
		List<TransactionStatus> nextStatuses = flowConfigurationService.getNextStatuses(
				transaction.getFlowTargetId(), transaction.getFlowActionId(), transaction.getStatus());
		return nextStatuses;
	}

	private List<TransactionStep> findValidSteps(
			List<TransactionStatus> possibleStatuses, TransactionExecutionContext context) {
		List<TransactionStep> validSteps = new ArrayList<>();
		for (TransactionStatus status : possibleStatuses) {
			TransactionStep step = stepFactory.getStepForStatus(status);
			if (step != null && step.precondition(context)) {
				validSteps.add(step);
			}
		}
		return validSteps;
	}

	private TransactionExecutionContext executeStep(TransactionExecutionContext context, TransactionStep step) {
		try {
			context = step.execute(context);
			context.setFirstExecution(false);
			return executeNextStep(context);
		} catch (Exception e) {
			if (context.isFirstExecution()) {
				throw e;
			}
			return context;
		}
	}

	private void verifyTransition(Transaction transaction, TransactionStatus targetStatus) {
		TransactionStatus currentStatus = transaction.getStatus();
		if (!flowConfigurationService.isValidTransition(
				transaction.getFlowTargetId(), transaction.getFlowActionId(), currentStatus, targetStatus)) {
			throw new AppException("Transaction transition not valid", ErrorCode.TRANSACTION_INVALID_STATUS);
		}
	}
}
