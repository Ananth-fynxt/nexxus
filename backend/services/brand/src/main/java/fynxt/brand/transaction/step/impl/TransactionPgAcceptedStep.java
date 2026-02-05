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
public class TransactionPgAcceptedStep extends AbstractTransactionStep {

	public TransactionPgAcceptedStep(
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {
		super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
	}

	@Override
	protected boolean customPrecondition(TransactionExecutionContext context) {
		// Custom business logic: check for successful PG redirect data
		if (context.getCustomData() != null && context.getCustomData().containsKey("pgRedirectData")) {
			DenoVMResult pgRedirectData = (DenoVMResult) context.getCustomData().get("pgRedirectData");
			return pgRedirectData != null && pgRedirectData.isSuccess();
		}
		return false;
	}

	@Override
	protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
		return context;
	}

	@Override
	public TransactionStatus getDestinationStatus() {
		return TransactionStatus.PG_ACCEPTED;
	}
}
