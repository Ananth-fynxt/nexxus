package fynxt.brand.transaction.service;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.PspService;
import fynxt.brand.session.service.SessionService;
import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.dto.TransactionResponseDto;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.orchestrator.TransactionOrchestrator;
import fynxt.brand.transaction.service.mappers.TransactionMapper;
import fynxt.denovm.dto.DenoVMResult;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionFlowService {
	private final TransactionOrchestrator orchestrator;
	private final TransactionMapper transactionMapper;
	private final PspService pspService;
	private final SessionService sessionService;

	@Value("${api.widget-url}")
	private String widgetUrl;

	public TransactionDto createTransaction(TransactionDto transactionDto) {
		TransactionExecutionContext resultContext = orchestrator.createTransaction(transactionDto);
		return transactionMapper.toDto(resultContext);
	}

	public TransactionResponseDto createTransactionWithSession(
			TransactionDto transactionDto, UUID brandId, UUID environmentId) {
		TransactionDto createdTransaction = createTransaction(transactionDto);

		Object txnResponse = null;
		if (createdTransaction.getCustomData() != null) {
			txnResponse = createdTransaction.getCustomData().get("vmExecutionResponse");
		}

		boolean isSuccess = false;
		Object txnMeta = null;
		String txnError = null;
		Object txnData = null;
		if (txnResponse instanceof DenoVMResult) {
			DenoVMResult denoResult = (DenoVMResult) txnResponse;
			isSuccess = denoResult.isSuccess();
			txnMeta = denoResult.getMeta();
			txnError = denoResult.getError();
			txnData = denoResult.getData();
		}

		TransactionResponseDto transactionCreateResponseDto = TransactionResponseDto.builder()
				.txnId(createdTransaction.getTxnId())
				.txnMeta(txnMeta)
				.txnError(txnError)
				.txnSuccess(isSuccess)
				.build();

		if (isSuccess && txnResponse != null) {
			try {
				Integer pspTimeoutSeconds = null;
				if (createdTransaction.getPspId() != null) {
					Psp psp = pspService.getPspIfEnabled(createdTransaction.getPspId());
					pspTimeoutSeconds = psp.getTimeout();
				}

				String sessionToken = sessionService.createSessionFromTransaction(
						txnData,
						brandId,
						environmentId,
						createdTransaction.getTxnId(),
						createdTransaction.getVersion(),
						pspTimeoutSeconds);

				transactionCreateResponseDto.setSessionUrl(widgetUrl + "/" + sessionToken);
			} catch (Exception e) {
				log.error(
						"TxnId: {} \nVersion: {} \nError: {}",
						createdTransaction.getTxnId(),
						createdTransaction.getVersion(),
						e.getMessage());
			}
		}

		return transactionCreateResponseDto;
	}

	public TransactionDto moveToStatus(TransactionDto transactionDto, TransactionStatus status) {
		Transaction transaction = transactionMapper.toEntity(transactionDto);
		TransactionExecutionContext context = TransactionExecutionContext.builder()
				.transaction(transaction)
				.isFirstExecution(false)
				.build();
		TransactionExecutionContext resultContext = orchestrator.transitionToStatus(context, status);
		return transactionMapper.toDto(resultContext.getTransaction());
	}
}
