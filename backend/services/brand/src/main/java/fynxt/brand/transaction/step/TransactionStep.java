package fynxt.brand.transaction.step;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.enums.TransactionStatus;

public interface TransactionStep {
	boolean precondition(TransactionExecutionContext context);

	TransactionExecutionContext execute(TransactionExecutionContext context);

	TransactionStatus getDestinationStatus();
}
