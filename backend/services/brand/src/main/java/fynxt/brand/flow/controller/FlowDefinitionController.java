package fynxt.brand.flow.controller;

import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.flowdefinition.dto.FlowDefinitionDto;
import fynxt.flowdefinition.service.FlowDefinitionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow-definitions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Flow Definitions")
public class FlowDefinitionController {

	private final FlowDefinitionService flowDefinitionService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new flow definition")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody FlowDefinitionDto dto) {
		return responseBuilder.created(flowDefinitionService.create(dto), "Created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all flow definitions")
	public ResponseEntity<ApiResponse<Object>> readAll() {
		return responseBuilder.get(flowDefinitionService.readAll(), "Success");
	}

	@GetMapping("/flow-target/{flowTargetId}")
	@Operation(summary = "Get all flow definitions by flow target ID")
	public ResponseEntity<ApiResponse<Object>> readAllByFlowTargetId(
			@Parameter(required = true, example = "flow_target_001") @PathVariable("flowTargetId") @NotBlank String flowTargetId) {
		return responseBuilder.get(flowDefinitionService.readAllByFlowTargetId(flowTargetId), "Success");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get flow definition by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "flow_def_001") @PathVariable("id") @NotBlank String id) {
		return responseBuilder.get(flowDefinitionService.read(id), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing flow definition")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "flow_def_001") @PathVariable("id") String id,
			@Parameter(required = true) @Validated @RequestBody FlowDefinitionDto dto) {
		dto.setId(id);
		return responseBuilder.updated(flowDefinitionService.update(id, dto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a flow definition")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "flow_def_001") @PathVariable("id") @NotBlank String id) {
		flowDefinitionService.delete(id);
		return responseBuilder.deleted("Flow definition deleted successfully");
	}
}
