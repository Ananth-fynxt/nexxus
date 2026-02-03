package fynxt.brand.fi.controller;

import fynxt.brand.fi.dto.FiDto;
import fynxt.brand.fi.service.FiService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresScope;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fi")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI"})
@Tag(name = "Financial Institutions")
public class FiController {

	private final FiService fiService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new Financial Institution")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody @NotNull FiDto dto) {
		return responseBuilder.created(fiService.create(dto), "Financial Institution created successfully");
	}
}
