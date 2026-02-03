package fynxt.brand.environment.controller;

import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.service.EnvironmentService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresScope;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/environments")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND", "EXTERNAL"})
@Tag(name = "Environments")
public class EnvironmentController {

	private final EnvironmentService environmentService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new environment")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody @NotNull EnvironmentDto environmentDto) {
		return responseBuilder.created(environmentService.create(environmentDto), "Environment created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all environments")
	public ResponseEntity<ApiResponse<Object>> readAll() {
		return responseBuilder.getAll(environmentService.readAll(), "Environments retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get environment by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id) {
		return responseBuilder.get(environmentService.read(id), "Environment retrieved successfully");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing environment")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
			@Parameter(required = true) @Validated @NotNull @RequestBody EnvironmentDto environmentDto) {
		environmentDto.setId(id);
		return responseBuilder.updated(environmentService.update(environmentDto), "Environment updated successfully");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete an environment")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id) {
		environmentService.delete(id);
		return responseBuilder.deleted("Environment deleted successfully");
	}

	@GetMapping("/brand/{brandId}")
	@Operation(summary = "Get environments by brand ID")
	public ResponseEntity<ApiResponse<Object>> findByBrandId(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000")
					@PathVariable("brandId")
					@Validated
					@NotNull UUID brandId) {
		return responseBuilder.getAll(environmentService.findByBrandId(brandId), "Environments retrieved successfully");
	}

	@PutMapping("/{id}/rotate-secret")
	@Operation(summary = "Rotate environment secret")
	public ResponseEntity<ApiResponse<Object>> rotateSecret(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id) {
		return responseBuilder.updated(environmentService.rotateSecret(id), "Environment secret rotated successfully");
	}

	@GetMapping("/{id}/credentials")
	@Operation(summary = "Get environment credentials (secret and token)")
	public ResponseEntity<ApiResponse<Object>> readCredentials(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id) {
		return responseBuilder.get(
				environmentService.readCredentials(id), "Environment credentials retrieved successfully");
	}
}
