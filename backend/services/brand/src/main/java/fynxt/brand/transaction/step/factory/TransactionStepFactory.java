package fynxt.brand.transaction.step.factory;

import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.step.TransactionStep;

import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TransactionStepFactory {

	private final EnumMap<TransactionStatus, TransactionStep> transactionStepMap;

	public TransactionStepFactory(List<TransactionStep> transactionSteps) {
		this.transactionStepMap = new EnumMap<>(TransactionStatus.class);
		for (TransactionStep transactionStep : transactionSteps) {
			transactionStepMap.put(transactionStep.getDestinationStatus(), transactionStep);
		}
	}

	public TransactionStep getStepForStatus(TransactionStatus status) {
		TransactionStep step = transactionStepMap.get(status);
		if (step == null) {}
		return step;
	}
}
