package fynxt.brand.health.controller;

import fynxt.brand.health.service.HealthService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Validated
@Tag(name = "Health")
public class HealthController {

	private final HealthService healthService;
	private final ResponseBuilder responseBuilder;

	@GetMapping
	@Operation(summary = "Health check (GET)")
	public ResponseEntity<ApiResponse<Object>> health() {
		return responseBuilder.get(healthService.getHealthStatus(), "Success");
	}

	@PostMapping
	@Operation(summary = "Health check (POST)")
	public ResponseEntity<ApiResponse<Object>> healthPost() {
		return responseBuilder.get(healthService.getHealthStatus(), "Success");
	}
}
