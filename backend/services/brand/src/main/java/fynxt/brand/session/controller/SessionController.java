package fynxt.brand.session.controller;

import fynxt.brand.session.dto.TransactionSession;
import fynxt.brand.session.service.SessionService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Sessions")
public class SessionController {

	private final SessionService sessionService;
	private final ResponseBuilder responseBuilder;

	@GetMapping("/{token}")
	@Operation(summary = "Get transaction response by session token")
	public ResponseEntity<ApiResponse<Object>> getTransactionResponse(
			@Parameter(required = true, example = "session_token_abc123") @PathVariable("token") @NotBlank String sessionToken) {

		TransactionSession response = sessionService.getTransactionResponseBySessionToken(sessionToken);

		return responseBuilder.get(response, "Transaction response retrieved successfully");
	}

	@GetMapping("/{token}/validate")
	@Operation(summary = "Validate session token")
	public ResponseEntity<ApiResponse<Object>> validateSession(
			@Parameter(required = true, example = "session_token_abc123") @PathVariable("token") @NotBlank String sessionToken) {

		boolean isValid = sessionService.validateSession(sessionToken);

		return responseBuilder.get(
				java.util.Map.of("valid", isValid), isValid ? "Session is valid" : "Session is invalid or expired");
	}
}
