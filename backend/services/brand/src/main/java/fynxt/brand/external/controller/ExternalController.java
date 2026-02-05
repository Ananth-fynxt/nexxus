package fynxt.brand.external.controller;

import fynxt.brand.external.util.ExternalRequestExtractor;
import fynxt.brand.external.util.ExternalRequestHandler;
import fynxt.common.http.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
@Validated
@Tag(name = "External")
public class ExternalController {

	private final ExternalRequestHandler requestHandler;
	private final ExternalRequestExtractor requestExtractor;

	@PostMapping("/inbound/r/{step}/{token}/{tnxId}")
	@Operation(summary = "Handle redirect POST request with transaction ID")
	public RedirectView handleReadPost(
			HttpServletRequest request,
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "txn_001") @PathVariable("tnxId") @NotBlank String tnxId,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		ExternalRequestExtractor.RequestData requestData = requestExtractor.extractRequestData(request);
		String cleanTnxId = requestExtractor.cleanTransactionId(tnxId);

		return requestHandler.processRedirectRequest(
				requestData.getRequestBody(),
				queryParams,
				token,
				cleanTnxId,
				step,
				requestData.getHeaders(),
				requestData.getRawBody());
	}

	@PostMapping("/inbound/r/{step}/{token}")
	@Operation(summary = "Handle redirect POST request without transaction ID")
	public RedirectView handleReadPostNoTnxId(
			HttpServletRequest request,
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		ExternalRequestExtractor.RequestData requestData = requestExtractor.extractRequestData(request);
		String txnId = requestExtractor.extractTransactionId(requestData.getRequestBody(), queryParams);

		return requestHandler.processRedirectRequest(
				requestData.getRequestBody(),
				queryParams,
				token,
				txnId,
				step,
				requestData.getHeaders(),
				requestData.getRawBody());
	}

	@GetMapping("/inbound/r/{step}/{token}/{tnxId}")
	@Operation(summary = "Handle redirect GET request with transaction ID")
	public RedirectView handleReadGet(
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "txn_001") @PathVariable("tnxId") @NotBlank String tnxId,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		String cleanTnxId = requestExtractor.cleanTransactionId(tnxId);
		return requestHandler.processRedirectRequest(null, queryParams, token, cleanTnxId, step, new HashMap<>(), null);
	}

	@GetMapping("/inbound/r/{step}/{token}")
	@Operation(summary = "Handle redirect GET request without transaction ID")
	public RedirectView handleReadGetNoTnxId(
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		String txnId = requestExtractor.extractTransactionId(null, queryParams);
		return requestHandler.processRedirectRequest(null, queryParams, token, txnId, step, new HashMap<>(), null);
	}

	@PostMapping("/inbound/w/{step}/{token}/{tnxId}")
	@Operation(summary = "Handle webhook POST request with transaction ID")
	public ResponseEntity<ApiResponse<Object>> handleWritePost(
			HttpServletRequest request,
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "txn_001") @PathVariable("tnxId") @NotBlank String tnxId) {
		ExternalRequestExtractor.RequestData requestData = requestExtractor.extractRequestData(request);
		String cleanTnxId = requestExtractor.cleanTransactionId(tnxId);

		return requestHandler.processInboundRequest(
				requestData.getRequestBody(),
				queryParams,
				token,
				cleanTnxId,
				step,
				requestData.getHeaders(),
				requestData.getRawBody());
	}

	@PostMapping("/inbound/w/{step}/{token}")
	@Operation(summary = "Handle webhook POST request without transaction ID")
	public ResponseEntity<ApiResponse<Object>> handleWritePostNoTnxId(
			HttpServletRequest request,
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		ExternalRequestExtractor.RequestData requestData = requestExtractor.extractRequestData(request);
		String txnId = requestExtractor.extractTransactionId(requestData.getRequestBody(), queryParams);

		return requestHandler.processInboundRequest(
				requestData.getRequestBody(),
				queryParams,
				token,
				txnId,
				step,
				requestData.getHeaders(),
				requestData.getRawBody());
	}

	@GetMapping("/inbound/w/{step}/{token}/{tnxId}")
	@Operation(summary = "Handle webhook GET request with transaction ID")
	public ResponseEntity<ApiResponse<Object>> handleWriteGet(
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "txn_001") @PathVariable("tnxId") @NotBlank String tnxId,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		String cleanTnxId = requestExtractor.cleanTransactionId(tnxId);
		return requestHandler.processInboundRequest(null, queryParams, token, cleanTnxId, step, new HashMap<>(), null);
	}

	@GetMapping("/inbound/w/{step}/{token}")
	@Operation(summary = "Handle webhook GET request without transaction ID")
	public ResponseEntity<ApiResponse<Object>> handleWriteGetNoTnxId(
			@Parameter(required = false) @RequestParam(required = false) Map<String, Object> queryParams,
			@Parameter(required = true, example = "env_token_abc123") @PathVariable("token") @NotBlank String token,
			@Parameter(required = true, example = "payment") @PathVariable("step") @NotBlank String step) {
		String txnId = requestExtractor.extractTransactionId(null, queryParams);
		return requestHandler.processInboundRequest(null, queryParams, token, txnId, step, new HashMap<>(), null);
	}
}
