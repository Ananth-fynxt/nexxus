package fynxt.brand.brand.controller;

import fynxt.brand.brand.dto.BrandDto;
import fynxt.brand.brand.service.BrandService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
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
@RequestMapping("/brands")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Brands")
public class BrandController {

	private final BrandService brandService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new brand")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody @NotNull BrandDto brandDto) {
		return responseBuilder.created(brandService.create(brandDto), "Brand created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all brands")
	public ResponseEntity<ApiResponse<Object>> readAll() {
		return responseBuilder.getAll(brandService.readAll(), "Brands retrieved successfully");
	}

	@GetMapping("/by-fi/{fiId}")
	@Operation(summary = "Get brands by Financial Institution ID")
	public ResponseEntity<ApiResponse<Object>> readByFiId(
			@Parameter(required = true, example = "fi_001") @PathVariable("fiId") @Validated @NotBlank String fiId) {
		return responseBuilder.getAll(brandService.findByFiId(Short.parseShort(fiId)), "Brands retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get brand by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "brn_001") @PathVariable("id") @Validated UUID id) {
		return responseBuilder.get(brandService.read(id), "Brand retrieved successfully");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing brand")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
			@Parameter(required = true) @Validated @NotNull @RequestBody BrandDto brandDto) {
		brandDto.setId(id);
		return responseBuilder.updated(brandService.update(brandDto), "Brand updated successfully");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a brand")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id) {
		brandService.delete(id);
		return responseBuilder.deleted("Brand deleted successfully");
	}
}
