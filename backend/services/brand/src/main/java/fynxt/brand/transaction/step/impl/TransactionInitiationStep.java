package fynxt.brand.transaction.step.impl;

import fynxt.brand.external.dto.VmExecutionDto;
import fynxt.brand.external.service.VMExecuteService;
import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionFlowConfigurationService;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.brand.transaction.step.AbstractTransactionStep;
import fynxt.denovm.dto.DenoVMResult;

import org.springframework.stereotype.Component;

@Component
public class TransactionInitiationStep extends AbstractTransactionStep {

	private final VMExecuteService vmExecuteService;

	public TransactionInitiationStep(
			VMExecuteService vmExecuteService,
			TransactionRepository transactionRepository,
			TransactionMapper transactionMapper,
			TransactionFlowConfigurationService transactionFlowConfigurationService) {
		super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
		this.vmExecuteService = vmExecuteService;
	}

	@Override
	protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
		TransactionDto transactionDto = transactionMapper.toDto(context.getTransaction());

		VmExecutionDto vmExecutionDto = VmExecutionDto.builder()
				.pspId(transactionDto.getPspId())
				.amount(transactionDto.getTxnAmount().longValue())
				.currency(transactionDto.getTxnCurrency())
				.brandId(transactionDto.getBrandId())
				.environmentId(transactionDto.getEnvironmentId())
				.step("initiate")
				.flowActionId(transactionDto.getFlowActionId())
				.transactionId(transactionDto.getTxnId())
				.executePayload(transactionDto.getExecutePayload())
				.build();

		DenoVMResult response = vmExecuteService.executeVmRequest(vmExecutionDto);
		context.getCustomData().put("vmExecutionResponse", response);
		return context;
	}

	@Override
	public TransactionStatus getDestinationStatus() {
		return TransactionStatus.INITIATED;
	}
}
