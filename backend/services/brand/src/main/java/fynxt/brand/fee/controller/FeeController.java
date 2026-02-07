package fynxt.brand.fee.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.fee.service.FeeService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;
import fynxt.permission.annotations.RequiresScope;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Fees")
public class FeeController {

	private final FeeService feeService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new fee")
	@RequiresPermission(module = "fees", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true) @Validated @RequestBody FeeDto feeDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		feeDto.setBrandId(brandIdValue);
		feeDto.setEnvironmentId(environmentIdValue);
		return responseBuilder.created(feeService.create(feeDto), "Created successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get fee by ID")
	@RequiresPermission(module = "fees", action = "read")
	public ResponseEntity<ApiResponse<Object>> readLatest(
			@Parameter(required = true, example = "fee_001") @PathVariable("id") @NotBlank String id) {
		Integer feeId = Integer.parseInt(id);
		return responseBuilder.get(feeService.readLatest(feeId), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all fees by brand and environment")
	@RequiresPermission(module = "fees", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(feeService.readByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@GetMapping("/psp/{pspId}")
	@Operation(summary = "Get all fees by PSP ID")
	@RequiresPermission(module = "fees", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByPspId(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("pspId")
					UUID pspId) {
		return responseBuilder.get(feeService.readByPspId(pspId), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing fee")
	@RequiresPermission(module = "fees", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true, example = "fee_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true) @Validated @RequestBody FeeDto feeDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		feeDto.setBrandId(brandIdValue);
		feeDto.setEnvironmentId(environmentIdValue);
		Integer feeId = Integer.parseInt(id);
		feeDto.setId(feeId);
		return responseBuilder.updated(feeService.update(feeId, feeDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a fee")
	@RequiresPermission(module = "fees", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "fee_001") @PathVariable("id") @NotBlank String id) {
		Integer feeId = Integer.parseInt(id);
		feeService.delete(feeId);
		return responseBuilder.deleted("Fee deleted successfully");
	}
}
