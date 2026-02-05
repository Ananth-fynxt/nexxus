package fynxt.brand.transaction.orchestrator;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.enums.TransactionStatus;

public interface TransactionOrchestrator {

	TransactionExecutionContext createTransaction(TransactionDto transactionDto);

	TransactionExecutionContext executeNextStep(TransactionExecutionContext context);

	TransactionExecutionContext transitionToStatus(TransactionExecutionContext context, TransactionStatus targetStatus);
}
