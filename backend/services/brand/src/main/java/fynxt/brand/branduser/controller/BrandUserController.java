package fynxt.brand.branduser.controller;

import fynxt.brand.branduser.dto.BrandUserDto;
import fynxt.brand.branduser.service.BrandUserService;
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
@RequestMapping("${api.prefix}/brand-users")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Brand Users")
public class BrandUserController {

	private final BrandUserService brandUserService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new brand user")
	@RequiresPermission(module = "brand_users", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody @NotNull BrandUserDto dto) {
		return responseBuilder.created(brandUserService.create(dto), "Brand user created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all brand users by brand and environment")
	@RequiresPermission(module = "brand_users", action = "read")
	public ResponseEntity<ApiResponse<Object>> readAll(
			@Parameter(hidden = true) @RequestHeader("X-BRAND-ID") @NotNull UUID brandId,
			@Parameter(hidden = true) @RequestHeader("X-ENV-ID") @NotNull UUID environmentId) {
		return responseBuilder.getAll(
				brandUserService.readAll(brandId, environmentId), "Brand users retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get brand user by ID")
	@RequiresPermission(module = "brand_users", action = "read")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "brand_user_001") @PathVariable("id") @Validated @NotBlank String id) {
		Integer userId = Integer.parseInt(id);
		return responseBuilder.get(brandUserService.read(userId), "Brand user retrieved successfully");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing brand user")
	@RequiresPermission(module = "brand_users", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "brand_user_001") @NotBlank @PathVariable String id,
			@Parameter(required = true) @Validated @NotNull @RequestBody BrandUserDto brandUserDto) {
		brandUserDto.setId(Integer.parseInt(id));
		return responseBuilder.updated(brandUserService.update(brandUserDto), "Brand user updated successfully");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a brand user")
	@RequiresPermission(module = "brand_users", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "brand_user_001") @NotBlank @PathVariable("id") String id) {
		Integer userId = Integer.parseInt(id);
		brandUserService.delete(userId);
		return responseBuilder.deleted("Brand user deleted successfully");
	}
}
