package fynxt.brand.routingrule.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.dto.UpdateRoutingRuleDto;
import fynxt.brand.routingrule.service.RoutingRuleService;
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
@RequestMapping("/routing-rules")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Routing Rules")
public class RoutingRuleController {

	private final RoutingRuleService routingRuleService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new routing rule")
	@RequiresPermission(module = "routing_rules", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody RoutingRuleDto routingRuleDto) {
		return responseBuilder.created(routingRuleService.create(routingRuleDto), "Created successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get routing rule by ID")
	@RequiresPermission(module = "routing_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> getById(
			@Parameter(required = true, example = "routing_rule_001") @PathVariable String id) {
		return responseBuilder.get(routingRuleService.getById(Integer.parseInt(id)), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing routing rule")
	@RequiresPermission(module = "routing_rules", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "routing_rule_001") @PathVariable String id,
			@Parameter(required = true) @Validated @RequestBody UpdateRoutingRuleDto updateRoutingRuleDto) {
		return responseBuilder.updated(
				routingRuleService.update(Integer.parseInt(id), updateRoutingRuleDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a routing rule")
	@RequiresPermission(module = "routing_rules", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "routing_rule_001") @PathVariable("id") String id) {
		routingRuleService.delete(Integer.parseInt(id));
		return responseBuilder.deleted("Routing rule deleted successfully");
	}

	@GetMapping
	@Operation(summary = "Get all routing rules by brand and environment")
	@RequiresPermission(module = "routing_rules", action = "read")
	public ResponseEntity<ApiResponse<Object>> readAllByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				routingRuleService.readAllByBrandAndEnvironment(brandIdValue, environmentIdValue), "Success");
	}
}
