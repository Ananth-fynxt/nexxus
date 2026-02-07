package fynxt.brand.flow.controller;

import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.flowtype.dto.FlowTypeDto;
import fynxt.flowtype.service.FlowTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow-types")
@RequiredArgsConstructor
@Validated
@Tag(name = "Flow Types")
public class FlowTypeController {

	private final FlowTypeService flowTypeService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new flow type")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody FlowTypeDto dto) {
		return responseBuilder.created(flowTypeService.create(dto), "Created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all flow types")
	public ResponseEntity<ApiResponse<Object>> readAll() {
		return responseBuilder.get(flowTypeService.readAll(), "Success");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get flow type by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("id") @NotBlank String id) {
		return responseBuilder.get(flowTypeService.read(id), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing flow type")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("id") String id,
			@Parameter(required = true) @Validated @RequestBody FlowTypeDto dto) {
		dto.setId(id);
		return responseBuilder.updated(flowTypeService.update(id, dto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a flow type")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("id") @NotBlank String id) {
		flowTypeService.delete(id);
		return responseBuilder.deleted("Flow type deleted successfully");
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Get flow type by name")
	public ResponseEntity<ApiResponse<Object>> findByName(
			@Parameter(required = true, example = "Payment") @PathVariable("name") @NotBlank String name) {
		return responseBuilder.get(flowTypeService.findByName(name), "Success");
	}
}
