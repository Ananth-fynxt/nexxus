package fynxt.brand.request.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.request.service.RequestService;
import fynxt.common.enums.ErrorCode;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresScope;

import java.util.Collections;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@RequiresScope({"EXTERNAL"})
@Tag(name = "Requests")
public class RequestController {

	private final RequestService requestService;
	private final ResponseBuilder responseBuilder;

	@PostMapping("/fetch-psp")
	@Operation(summary = "Fetch Payment Service Provider (PSP)")
	public ResponseEntity<ApiResponse<Object>> fetchPsp(
			@Parameter(required = true) @Validated @RequestBody RequestInputDto requestInputDto,
			HttpServletRequest httpRequest) {
		UUID brandId = BrandEnvironmentContextHolder.getBrandId();
		UUID environmentId = BrandEnvironmentContextHolder.getEnvironmentId();

		if (brandId == null || environmentId == null) {
			return responseBuilder.error(
					ErrorCode.MISSING_REQUIRED_PARAMETER,
					"Brand ID and Environment ID must be provided via authentication context",
					HttpStatus.BAD_REQUEST,
					Collections.emptyList());
		}

		requestInputDto.setBrandId(brandId);
		requestInputDto.setEnvironmentId(environmentId);

		requestInputDto.setClientIpAddress(getClientIpAddress(httpRequest));

		return responseBuilder.get(requestService.fetchPsp(requestInputDto), "Success");
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty()) {
			return xRealIp;
		}
		return request.getRemoteAddr();
	}
}
