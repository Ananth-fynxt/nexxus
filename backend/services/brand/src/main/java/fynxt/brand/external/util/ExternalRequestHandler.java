package fynxt.brand.external.util;

import fynxt.brand.external.service.ExternalService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

@Component
@RequiredArgsConstructor
public class ExternalRequestHandler {

	private final ExternalService externalService;
	private final ResponseBuilder responseBuilder;
	private final ExternalRequestMapper requestMapper;

	public RedirectView processRedirectRequest(
			Map<String, Object> requestBody,
			Map<String, Object> queryParams,
			String token,
			String tnxId,
			String step,
			Map<String, Object> headers,
			String rawBody) {

		Map<String, Object> externalDto =
				requestMapper.buildExternalDto(token, tnxId, step, requestBody, queryParams, headers, rawBody);

		Object result = externalService.read(externalDto);

		String redirectUrl = externalService.extractRedirectUrl(result, token, tnxId, step);

		if (redirectUrl == null) {
			redirectUrl = externalService.getEnvironmentOrigin(tnxId);
		}

		RedirectView redirectView = new RedirectView(redirectUrl);
		redirectView.setStatusCode(org.springframework.http.HttpStatus.FOUND);

		return redirectView;
	}

	public ResponseEntity<ApiResponse<Object>> processInboundRequest(
			Map<String, Object> requestBody,
			Map<String, Object> queryParams,
			String token,
			String tnxId,
			String step,
			Map<String, Object> headers,
			String rawBody) {

		Map<String, Object> externalDto =
				requestMapper.buildExternalDto(token, tnxId, step, requestBody, queryParams, headers, rawBody);

		return responseBuilder.get(externalService.read(externalDto), "Success");
	}
}
