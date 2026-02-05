package fynxt.brand.transaction.step.impl;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.brand.transaction.step.AbstractTransactionStep;
import fynxt.denovm.dto.DenoVMResult;

import org.springframework.stereotype.Component;

@Component
public class TransactionPgFailedStep extends AbstractTransactionStep {

	public TransactionPgFailedStep(
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {
		super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
	}

	@Override
	protected boolean customPrecondition(TransactionExecutionContext context) {
		// Custom business logic: check for successful PG webhook data
		DenoVMResult pgWebhookData = (DenoVMResult) context.getCustomData().get("pgWebhookData");
		return pgWebhookData != null && !pgWebhookData.isSuccess();
	}

	@Override
	protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
		return context;
	}

	@Override
	public TransactionStatus getDestinationStatus() {
		return TransactionStatus.PG_FAILED;
	}
}
