package fynxt.brand.transaction.step.impl;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.brand.transaction.step.AbstractTransactionStep;

import org.springframework.stereotype.Component;

@Component
public class TransactionFailedStep extends AbstractTransactionStep {

	public TransactionFailedStep(
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {
		super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
	}

	@Override
	protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
		return context;
	}

	@Override
	public TransactionStatus getDestinationStatus() {
		return TransactionStatus.FAILED;
	}
}
