package fynxt.brand.external.util;

import fynxt.brand.shared.util.RequestBodyExtractor;
import fynxt.brand.shared.util.ValidationUtils;
import fynxt.brand.transaction.entity.TransactionIdGenerator;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRequestExtractor {

	private final RequestBodyExtractor requestBodyExtractor;

	public RequestData extractRequestData(HttpServletRequest request) {
		Map<String, Object> requestBody = requestBodyExtractor.extractRequestBody(request);
		Map<String, Object> headers = extractHeaders(request);
		String rawBody = requestBodyExtractor.extractRawBody(request);

		return new RequestData(requestBody, headers, rawBody);
	}

	public Map<String, Object> extractHeaders(HttpServletRequest request) {
		Map<String, Object> headers = new HashMap<>();
		if (request != null) {
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerValue = request.getHeader(headerName);
				headers.put(headerName, headerValue);
			}
		}
		return headers;
	}

	public String extractTransactionId(Map<String, Object> requestBody, Map<String, Object> queryParams) {
		if (ValidationUtils.isNotNullOrEmpty(queryParams)) {
			String txnId = ValidationUtils.findValueByPrefix(queryParams, TransactionIdGenerator.TRANSACTION);
			if (ValidationUtils.isNotNullOrEmpty(txnId)) {
				return txnId;
			}
		}

		if (ValidationUtils.isNotNullOrEmpty(requestBody)) {
			String txnId = ValidationUtils.findValueByPrefix(requestBody, TransactionIdGenerator.TRANSACTION);
			if (ValidationUtils.isNotNullOrEmpty(txnId)) {
				return txnId;
			}
		}

		return null;
	}

	public String cleanTransactionId(String tnxId) {
		if (tnxId == null || tnxId.isEmpty()) {
			return tnxId;
		}

		try {
			String decoded = URLDecoder.decode(tnxId, StandardCharsets.UTF_8);

			int queryParamIndex = decoded.indexOf('?');
			if (queryParamIndex > 0) {
				return decoded.substring(0, queryParamIndex);
			}

			return decoded;
		} catch (Exception e) {
			int queryParamIndex = tnxId.indexOf('?');
			if (queryParamIndex > 0) {
				return tnxId.substring(0, queryParamIndex);
			}
			return tnxId;
		}
	}

	public static class RequestData {
		private final Map<String, Object> requestBody;
		private final Map<String, Object> headers;
		private final String rawBody;

		public RequestData(Map<String, Object> requestBody, Map<String, Object> headers, String rawBody) {
			this.requestBody = requestBody;
			this.headers = headers;
			this.rawBody = rawBody;
		}

		public Map<String, Object> getRequestBody() {
			return requestBody;
		}

		public Map<String, Object> getHeaders() {
			return headers;
		}

		public String getRawBody() {
			return rawBody;
		}
	}
}
