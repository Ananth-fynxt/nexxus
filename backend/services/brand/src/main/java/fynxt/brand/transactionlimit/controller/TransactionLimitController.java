package fynxt.brand.transactionlimit.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;
import fynxt.brand.transactionlimit.service.TransactionLimitService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;
import fynxt.permission.annotations.RequiresScope;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction-limits")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Transaction Limits")
public class TransactionLimitController {

	private final TransactionLimitService transactionLimitService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new transaction limit")
	@RequiresPermission(module = "transaction_limits", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody TransactionLimitDto transactionLimitDto) {
		return responseBuilder.created(transactionLimitService.create(transactionLimitDto), "Created successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get transaction limit by ID")
	@RequiresPermission(module = "transaction_limits", action = "read")
	public ResponseEntity<ApiResponse<Object>> readLatest(
			@Parameter(required = true, example = "txn_limit_001") @PathVariable("id") Integer id) {
		return responseBuilder.get(transactionLimitService.readLatest(id), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all transaction limits by brand and environment")
	@RequiresPermission(module = "transaction_limits", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				transactionLimitService.readByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@GetMapping("/psp/{pspId}")
	@Operation(summary = "Get all transaction limits by PSP ID")
	@RequiresPermission(module = "transaction_limits", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByPspId(
			@Parameter(required = true, example = "psp_001") @PathVariable("pspId") UUID pspId) {
		return responseBuilder.get(transactionLimitService.readByPspId(pspId), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing transaction limit")
	@RequiresPermission(module = "transaction_limits", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "txn_limit_001") @PathVariable("id") Integer id,
			@Parameter(required = true) @Validated @RequestBody TransactionLimitDto transactionLimitDto) {
		transactionLimitDto.setId(id);
		return responseBuilder.updated(transactionLimitService.update(id, transactionLimitDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a transaction limit")
	@RequiresPermission(module = "transaction_limits", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "txn_limit_001") @PathVariable("id") Integer id) {
		transactionLimitService.delete(id);
		return responseBuilder.deleted("Transaction limit deleted successfully");
	}
}
