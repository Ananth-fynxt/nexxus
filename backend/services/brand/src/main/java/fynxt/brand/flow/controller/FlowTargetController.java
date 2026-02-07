package fynxt.brand.flow.controller;

import fynxt.brand.flow.dto.FlowTargetResponseDto;
import fynxt.brand.flow.service.FlowTargetInputSchemaService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.flowtarget.dto.FlowTargetDto;
import fynxt.flowtarget.service.FlowTargetService;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-targets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Flow Targets")
public class FlowTargetController {

	private final FlowTargetService flowTargetService;
	private final ResponseBuilder responseBuilder;
	private final FlowTargetInputSchemaService inputSchemaService;

	@PostMapping
	@Operation(summary = "Create a new flow target")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId,
			@Parameter(required = true) @Validated @RequestBody FlowTargetDto dto) {
		dto.setFlowTypeId(flowTypeId);
		return responseBuilder.created(flowTargetService.create(dto), "Created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all flow targets for a flow type")
	public ResponseEntity<ApiResponse<Object>> readAll(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId) {

		List<FlowTargetDto> flowTargetDtos = flowTargetService.readAll(flowTypeId);

		List<FlowTargetResponseDto> responseDtos = flowTargetDtos.stream()
				.map(flowTargetDto -> {
					Object inputSchema = flowTargetDto.getInputSchema();
					return FlowTargetResponseDto.builder()
							.flowTarget(flowTargetDto)
							.currencies(inputSchemaService.extractCurrencies(inputSchema))
							.countries(inputSchemaService.extractCountries(inputSchema))
							.paymentMethods(inputSchemaService.extractPaymentMethods(inputSchema))
							.build();
				})
				.collect(Collectors.toList());

		return responseBuilder.get(responseDtos, "Success");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get flow target by ID")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "flow_target_001") @PathVariable("id") @NotBlank String id) {

		FlowTargetDto flowTargetDto = flowTargetService.read(id);

		Object inputSchema = flowTargetDto.getInputSchema();

		FlowTargetResponseDto responseDto = FlowTargetResponseDto.builder()
				.flowTarget(flowTargetDto)
				.currencies(inputSchemaService.extractCurrencies(inputSchema))
				.countries(inputSchemaService.extractCountries(inputSchema))
				.paymentMethods(inputSchemaService.extractPaymentMethods(inputSchema))
				.build();

		return responseBuilder.get(responseDto, "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing flow target")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "flow_type_001") @PathVariable("flowTypeId") @NotBlank String flowTypeId,
			@Parameter(required = true, example = "flow_target_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true) @Validated @RequestBody FlowTargetDto dto) {
		return responseBuilder.updated(flowTargetService.update(flowTypeId, id, dto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a flow target")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "flow_target_001") @PathVariable("id") @NotBlank String id) {
		flowTargetService.delete(id);
		return responseBuilder.deleted("Flow target deleted successfully");
	}
}
