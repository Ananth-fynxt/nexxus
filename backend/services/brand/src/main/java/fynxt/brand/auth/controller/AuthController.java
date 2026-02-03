package fynxt.brand.auth.controller;

import fynxt.auth.dto.AuthResponse;
import fynxt.auth.dto.LoginRequest;
import fynxt.brand.auth.service.AuthService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication")
public class AuthController {

	private final AuthService authService;
	private final ResponseBuilder responseBuilder;

	@PostMapping("/login")
	@Operation(summary = "User login")
	public ResponseEntity<ApiResponse<Object>> login(
			@Parameter(required = true) @Validated @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return responseBuilder.created(response, "Login successful");
	}

	@PostMapping("/token/refresh")
	@Operation(summary = "Refresh access token")
	public ResponseEntity<ApiResponse<Object>> refreshToken(
			@Parameter(required = true, example = "eyJhbGciOiJIUzI1NiJ9...") @RequestParam String refreshToken) {
		AuthResponse response = authService.refreshToken(refreshToken);
		return responseBuilder.created(response, "Token refreshed successfully");
	}

	@PostMapping("/logout")
	@Operation(summary = "User logout")
	public ResponseEntity<ApiResponse<Object>> logout(
			@Parameter(required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...") @RequestHeader("Authorization")
					String authorization) {
		String result = authService.logout(authorization);
		return responseBuilder.deleted(result);
	}
}
