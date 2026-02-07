package fynxt.brand.transaction.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.dto.TransactionResponseDto;
import fynxt.brand.transaction.dto.TransactionSearchCriteria;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.service.TransactionFlowService;
import fynxt.brand.transaction.service.TransactionService;
import fynxt.common.enums.ErrorCode;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transactions")
public class TransactionFlowController {

	private final TransactionFlowService transactionFlowService;
	private final TransactionService transactionService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new transaction")
	public ResponseEntity<ApiResponse<Object>> createTransaction(
			@Parameter(required = true) @Validated @RequestBody @NotNull TransactionDto transactionDto) {
		UUID brandIdValue = BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue = BrandEnvironmentContextHolder.getEnvironmentId();

		if (brandIdValue == null || environmentIdValue == null) {
			return responseBuilder.error(
					ErrorCode.MISSING_REQUIRED_PARAMETER,
					"Brand ID and Environment ID must be provided via authentication context",
					HttpStatus.BAD_REQUEST);
		}

		transactionDto.setBrandId(brandIdValue);
		transactionDto.setEnvironmentId(environmentIdValue);

		TransactionResponseDto responseDto =
				transactionFlowService.createTransactionWithSession(transactionDto, brandIdValue, environmentIdValue);

		return responseBuilder.get(responseDto, "Transaction created successfully");
	}

	@PutMapping("/{txnId}/status")
	@Operation(summary = "Update transaction status")
	@RequiresPermission(module = "transactions", action = "update")
	public ResponseEntity<ApiResponse<Object>> moveToStatus(
			@Parameter(required = true, example = "txn_001") @PathVariable("txnId") @NotNull String txnId,
			@Parameter(required = true, example = "SUCCESS") @RequestParam("status") @NotNull TransactionStatus status,
			@Parameter(required = true) @Validated @RequestBody @NotNull TransactionDto transactionDto) {
		UUID brandIdValue = BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue = BrandEnvironmentContextHolder.getEnvironmentId();

		if (brandIdValue == null || environmentIdValue == null) {
			return responseBuilder.error(
					ErrorCode.MISSING_REQUIRED_PARAMETER,
					"Brand ID and Environment ID must be provided via authentication context",
					HttpStatus.BAD_REQUEST);
		}

		transactionDto.setBrandId(brandIdValue);
		transactionDto.setEnvironmentId(environmentIdValue);

		return responseBuilder.updated(
				transactionFlowService.moveToStatus(transactionDto, status), "Transaction status updated successfully");
	}

	@GetMapping("/{txnId}")
	@Operation(summary = "Get transaction by ID")
	@RequiresPermission(module = "transactions", action = "read")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "txn_001") @PathVariable("txnId") @NotNull String txnId) {
		return responseBuilder.get(transactionService.read(txnId), "Transaction retrieved successfully");
	}

	@PostMapping("/search")
	@Operation(summary = "Search transactions by brand, environment, and criteria")
	@RequiresPermission(module = "transactions", action = "read")
	public ResponseEntity<ApiResponse<Object>> searchTransactions(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true) @Valid @RequestBody TransactionSearchCriteria criteria) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();

		Page<TransactionDto> pagedTransactions =
				transactionService.readByBrandIdAndEnvironmentId(brandIdValue, environmentIdValue, criteria);

		return responseBuilder.paginated(pagedTransactions, "Transactions retrieved successfully");
	}
}
