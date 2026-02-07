package fynxt.brand.flow.controller;

import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.flowaction.dto.FlowActionDto;
import fynxt.flowaction.service.FlowActionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-actions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Flow Actions")
public class FlowActionController {

	private final FlowActionService flowActionService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new flow action")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId,
			@Parameter(required = true) @Validated @RequestBody FlowActionDto dto) {
		dto.setFlowTypeId(flowTypeId);
		return responseBuilder.created(flowActionService.create(dto), "Created successfully");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing flow action")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "flow_action_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true) @Validated @RequestBody FlowActionDto dto) {
		dto.setId(id);
		return responseBuilder.updated(flowActionService.update(dto), "Success");
	}

	@GetMapping
	@Operation(summary = "Get all flow actions for a flow type")
	public ResponseEntity<ApiResponse<Object>> readAll(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId) {
		return responseBuilder.get(
				flowActionService.findByFlowTypeId(flowTypeId), "Flow actions retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get flow action by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(example = "flow_type_001") @PathVariable("flowTypeId") String flowTypeId,
			@Parameter(required = true, example = "flow_action_001") @PathVariable("id") @NotBlank String id) {
		return responseBuilder.get(flowActionService.read(id), "Success");
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Get flow action by name and flow type")
	public ResponseEntity<ApiResponse<Object>> findByNameAndFlowTypeId(
			@Parameter(required = true, example = "Process Payment") @PathVariable("name") @NotBlank String name,
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId) {
		return responseBuilder.get(flowActionService.findByNameAndFlowTypeId(name, flowTypeId), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a flow action")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "flow_action_001") @PathVariable("id") @NotBlank String id) {
		flowActionService.delete(id);
		return responseBuilder.deleted("Flow action deleted successfully");
	}
}
