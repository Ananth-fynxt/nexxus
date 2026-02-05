package fynxt.brand.transaction.step;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.TransactionException;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractTransactionStep implements TransactionStep {

	protected final TransactionRepository transactionRepository;
	protected final TransactionMapper transactionMapper;
	protected final TransactionFlowConfigurationService transactionFlowConfigurationService;

	protected AbstractTransactionStep(
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {

		this.transactionRepository = transactionRepository;
		this.transactionMapper = transactionMapper;
		this.transactionFlowConfigurationService = transactionFlowConfigurationService;
	}

	@Override
	public boolean precondition(TransactionExecutionContext context) {
		boolean statusTransitionAllowed = isStatusTransitionAllowed(context);
		boolean customLogicPassed = customPrecondition(context);
		return statusTransitionAllowed && customLogicPassed;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TransactionExecutionContext execute(TransactionExecutionContext context) {
		Transaction latestTransaction = getLatestTransactionForUpdate(context);
		context.setTransaction(latestTransaction);
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
		TransactionExecutionContext updatedContext = doExecute(context);
		return createAndSaveNewVersion(updatedContext);
	}

	@Override
	public abstract TransactionStatus getDestinationStatus();

	protected TransactionExecutionContext createAndSaveNewVersion(TransactionExecutionContext context) {
		Transaction currentTransaction = context.getTransaction();
		Transaction newTransaction =
				transactionMapper.createNewVersionedRecord(currentTransaction, getDestinationStatus());
		Transaction savedTransaction = transactionRepository.save(newTransaction);
		return context.toBuilder().transaction(savedTransaction).build();
	}

	protected abstract TransactionExecutionContext doExecute(TransactionExecutionContext context);

	protected boolean customPrecondition(TransactionExecutionContext context) {
		return true;
	}

	private boolean isStatusTransitionAllowed(TransactionExecutionContext context) {
		Transaction transaction = context.getTransaction();
		String flowTargetId = transaction.getFlowTargetId();
		String flowActionId = transaction.getFlowActionId();
		TransactionStatus currentStatus = transaction.getStatus();
		TransactionStatus destinationStatus = getDestinationStatus();

		if (flowTargetId == null || flowActionId == null) {
			return false;
		}

		try {
			return transactionFlowConfigurationService.isValidTransition(
					flowTargetId, flowActionId, currentStatus, destinationStatus);
		} catch (Exception e) {
			return false;
		}
	}

	private Transaction getLatestTransactionForUpdate(TransactionExecutionContext context) {
		return transactionRepository.findLatestByTxnIdForUpdate(context.getTxnId());
	}
}
