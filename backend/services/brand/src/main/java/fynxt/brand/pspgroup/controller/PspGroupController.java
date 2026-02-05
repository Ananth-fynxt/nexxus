package fynxt.brand.pspgroup.controller;

import fynxt.brand.pspgroup.dto.PspGroupDto;
import fynxt.brand.pspgroup.service.PspGroupService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;
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
@RequestMapping("/psp-groups")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "PSP Groups")
public class PspGroupController {

	private final PspGroupService pspGroupService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new PSP group")
	@RequiresPermission(module = "psp_groups", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody PspGroupDto pspGroupDto) {
		return responseBuilder.created(pspGroupService.create(pspGroupDto), "Created successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get PSP group by ID")
	@RequiresPermission(module = "psp_groups", action = "read")
	public ResponseEntity<ApiResponse<Object>> readLatest(
			@Parameter(required = true, example = "psp_group_001") @PathVariable("id") Integer id) {
		return responseBuilder.get(pspGroupService.readLatest(id), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all PSP groups by brand and environment")
	@RequiresPermission(module = "psp_groups", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
			@Parameter(hidden = true) @RequestHeader("X-BRAND-ID") @NotNull UUID brandId,
			@Parameter(hidden = true) @RequestHeader("X-ENV-ID") @NotNull UUID environmentId) {
		return responseBuilder.get(pspGroupService.readByBrandAndEnvironment(brandId, environmentId), "Success");
	}

	@GetMapping("/psp/{pspId}")
	@Operation(summary = "Get all PSP groups by PSP ID")
	@RequiresPermission(module = "psp_groups", action = "read")
	public ResponseEntity<ApiResponse<Object>> readByPspId(
			@Parameter(required = true, example = "psp_001") @PathVariable("pspId") UUID pspId) {
		return responseBuilder.get(pspGroupService.readByPspId(pspId), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing PSP group")
	@RequiresPermission(module = "psp_groups", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "psp_group_001") @PathVariable("id") Integer id,
			@Parameter(required = true) @Validated @RequestBody PspGroupDto pspGroupDto) {
		pspGroupDto.setId(id);
		return responseBuilder.updated(pspGroupService.update(id, pspGroupDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a PSP group")
	@RequiresPermission(module = "psp_groups", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "psp_group_001") @PathVariable("id") Integer id) {
		pspGroupService.delete(id);
		return responseBuilder.deleted("PSP group deleted successfully");
	}
}
