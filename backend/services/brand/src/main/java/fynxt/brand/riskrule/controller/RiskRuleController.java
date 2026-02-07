package fynxt.brand.riskrule.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.riskrule.service.RiskRuleService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;
import fynxt.permission.annotations.RequiresScope;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("risk-rules")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Risk Rules")
public class RiskRuleController {

	private final RiskRuleService riskRuleService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new risk rule")
	@RequiresPermission(module = "risk_rules", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true) @Validated @RequestBody RiskRuleDto riskRuleDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		riskRuleDto.setBrandId(brandIdValue);
		riskRuleDto.setEnvironmentId(environmentIdValue);
		return responseBuilder.created(riskRuleService.create(riskRuleDto), "Created successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get risk rule by ID (latest version)")
	@RequiresPermission(module = "risk_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> readLatest(
			@Parameter(required = true, example = "risk_rule_001") @PathVariable("id") @NotBlank String id) {
		return responseBuilder.get(riskRuleService.readLatest(Integer.parseInt(id)), "Success");
	}

	@GetMapping("/{id}/version/{version}")
	@Operation(summary = "Get risk rule by ID and version")
	@RequiresPermission(module = "risk_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "risk_rule_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true, example = "1") @PathVariable("version") @NotNull Integer version) {
		return responseBuilder.get(riskRuleService.read(Integer.parseInt(id), version), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all risk rules by brand and environment")
	@RequiresPermission(module = "risk_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				riskRuleService.readByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}

	@GetMapping("/psp/{pspId}")
	@Operation(summary = "Get all risk rules by PSP ID")
	@RequiresPermission(module = "risk_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByPspId(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("pspId")
					UUID pspId) {
		return responseBuilder.get(riskRuleService.readByPspId(pspId), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing risk rule")
	@RequiresPermission(module = "risk_rules", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId,
			@Parameter(required = true, example = "risk_rule_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true) @Validated @RequestBody RiskRuleDto riskRuleDto) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		riskRuleDto.setBrandId(brandIdValue);
		riskRuleDto.setEnvironmentId(environmentIdValue);
		return responseBuilder.updated(riskRuleService.update(Integer.parseInt(id), riskRuleDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a risk rule")
	@RequiresPermission(module = "risk_rules", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "risk_rule_001") @PathVariable("id") @NotBlank String id) {
		riskRuleService.delete(Integer.parseInt(id));
		return responseBuilder.deleted("Risk Rule deleted successfully");
	}
}
