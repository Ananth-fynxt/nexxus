package fynxt.brand.psp.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.psp.dto.PspDto;
import fynxt.brand.psp.dto.UpdatePspDto;
import fynxt.brand.psp.service.PspService;
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
@RequestMapping("/psps")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Payment Service Providers (PSPs)")
public class PspController {

	private final PspService pspService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new PSP")
	@RequiresPermission(module = "psps", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true) @Validated @RequestBody PspDto pspDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		pspDto.setBrandId(brandIdValue);
		pspDto.setEnvironmentId(environmentIdValue);
		return responseBuilder.created(pspService.create(pspDto), "Created successfully");
	}

	@GetMapping("/{pspId}")
	@Operation(summary = "Get PSP by ID")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getById(
			@Parameter(required = true, example = "psp_001") @Validated @PathVariable UUID pspId) {
		return responseBuilder.get(pspService.getById(pspId), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all PSPs by brand and environment")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(pspService.getByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@GetMapping("/{flowActionId}/{status}/{currency}")
	@Operation(summary = "Get PSPs by brand, environment, flow action, status, and currency")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true, example = "flow_action_001") @Validated @PathVariable @NotBlank String flowActionId,
			@Parameter(required = true, example = "ENABLED") @Validated @PathVariable @NotBlank String status,
			@Parameter(required = true, example = "USD") @Validated @PathVariable @NotBlank String currency) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				pspService.getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
						brandIdValue, environmentIdValue, status, currency, flowActionId),
				"Success");
	}

	@GetMapping("/{flowActionId}/{status}")
	@Operation(summary = "Get PSPs by brand, environment, flow action, and status")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getByBrandAndEnvironmentByStatusAndFlowAction(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true, example = "flow_action_001") @Validated @PathVariable @NotBlank String flowActionId,
			@Parameter(required = true, example = "ENABLED") @Validated @PathVariable @NotBlank String status) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				pspService.getByBrandAndEnvironmentByStatusAndFlowAction(
						brandIdValue, environmentIdValue, status, flowActionId),
				"Success");
	}

	@GetMapping("/currencies")
	@Operation(summary = "Get supported currencies by brand and environment")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getSupportedCurrenciesByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				pspService.getSupportedCurrenciesByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@GetMapping("/countries")
	@Operation(summary = "Get supported countries by brand and environment")
	@RequiresPermission(module = "psps", action = "read")
	public ResponseEntity<ApiResponse<Object>> getSupportedCountriesByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				pspService.getSupportedCountriesByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@PutMapping("/{pspId}")
	@Operation(summary = "Update an existing PSP")
	@RequiresPermission(module = "psps", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true, example = "psp_001") @Validated @PathVariable UUID pspId,
			@Parameter(required = true) @Validated @RequestBody UpdatePspDto pspDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		pspDto.setBrandId(brandIdValue);
		pspDto.setEnvironmentId(environmentIdValue);
		return responseBuilder.updated(pspService.update(pspId, pspDto), "Success");
	}

	@PutMapping("/{pspId}/{status}")
	@Operation(summary = "Update PSP status")
	@RequiresPermission(module = "psps", action = "update")
	public ResponseEntity<ApiResponse<Object>> updateStatus(
			@Parameter(required = true, example = "psp_001") @Validated @PathVariable UUID pspId,
			@Parameter(required = true, example = "ENABLED") @Validated @PathVariable @NotBlank String status) {
		return responseBuilder.updated(pspService.updateStatus(pspId, status), "Success");
	}
}
