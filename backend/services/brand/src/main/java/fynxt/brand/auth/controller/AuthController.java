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
@Tag(name = "Authentication", description = "Authentication endpoints for user login and token management")
public class AuthController {

	private final AuthService authService;
	private final ResponseBuilder responseBuilder;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user with email and password")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
			@Parameter(required = true, description = "Login credentials") @Validated @RequestBody
					LoginRequest request) {
		AuthResponse response = authService.login(request);
		return responseBuilder.successResponse(response);
	}

	@PostMapping("/token/refresh")
	@Operation(summary = "Refresh access token", description = "Generate a new access token using refresh token")
	public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
			@Parameter(required = true, description = "Refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
					@RequestParam
					String refreshToken) {
		AuthResponse response = authService.refreshToken(refreshToken);
		return responseBuilder.successResponse(response);
	}

	@PostMapping("/logout")
	@Operation(summary = "User logout", description = "Revoke all tokens for the authenticated user")
	public ResponseEntity<ApiResponse<String>> logout(
			@Parameter(
							required = true,
							description = "Authorization Bearer token",
							example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
					@RequestHeader("Authorization")
					String authorization) {
		String result = authService.logout(authorization);
		return responseBuilder.successResponse(result);
	}
}
