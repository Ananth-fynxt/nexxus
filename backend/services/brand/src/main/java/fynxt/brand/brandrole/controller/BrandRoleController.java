package fynxt.brand.brandrole.controller;

import fynxt.brand.brandrole.dto.BrandRoleDto;
import fynxt.brand.brandrole.service.BrandRoleService;
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
@RequestMapping("/brand-roles")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Brand Roles")
public class BrandRoleController {

	private final BrandRoleService brandRoleService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new brand role")
	@RequiresPermission(module = "brand_roles", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(hidden = true) @RequestHeader("X-BRAND-ID") @NotNull UUID brandId,
			@Parameter(hidden = true) @RequestHeader("X-ENV-ID") @NotNull UUID environmentId,
			@Parameter(required = true) @Validated @RequestBody @NotNull BrandRoleDto brandRoleDto) {
		brandRoleDto.setBrandId(brandId);
		brandRoleDto.setEnvironmentId(environmentId);
		return responseBuilder.created(brandRoleService.create(brandRoleDto), "Brand role created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all brand roles by brand and environment")
	@RequiresPermission(module = "brand_roles", action = "read")
	public ResponseEntity<ApiResponse<Object>> readAll(
			@Parameter(hidden = true) @RequestHeader("X-BRAND-ID") @NotNull UUID brandId,
			@Parameter(hidden = true) @RequestHeader("X-ENV-ID") @NotNull UUID environmentId) {
		return responseBuilder.getAll(
				brandRoleService.readAll(brandId, environmentId), "Brand roles retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get brand role by ID")
	@RequiresPermission(module = "brand_roles", action = "read")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "brand_role_001") @PathVariable("id") @Validated @NotBlank String id) {
		Integer roleId = Integer.parseInt(id);
		return responseBuilder.get(brandRoleService.read(roleId), "Brand role retrieved successfully");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing brand role")
	@RequiresPermission(module = "brand_roles", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(hidden = true) @RequestHeader("X-BRAND-ID") @NotNull UUID brandId,
			@Parameter(hidden = true) @RequestHeader("X-ENV-ID") @NotNull UUID environmentId,
			@Parameter(required = true, example = "brand_role_001") @NotBlank @PathVariable String id,
			@Parameter(required = true) @Validated @NotNull @RequestBody BrandRoleDto brandRoleDto) {
		brandRoleDto.setBrandId(brandId);
		brandRoleDto.setEnvironmentId(environmentId);
		brandRoleDto.setId(Integer.parseInt(id));
		return responseBuilder.updated(brandRoleService.update(brandRoleDto), "Brand role updated successfully");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a brand role")
	@RequiresPermission(module = "brand_roles", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "brand_role_001") @NotBlank @PathVariable("id") String id) {
		Integer roleId = Integer.parseInt(id);
		brandRoleService.delete(roleId);
		return responseBuilder.deleted("Brand role deleted successfully");
	}
}
