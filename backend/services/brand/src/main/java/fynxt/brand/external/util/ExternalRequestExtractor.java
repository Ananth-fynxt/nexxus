package fynxt.brand.external.util;

import fynxt.brand.transaction.entity.TransactionIdGenerator;

import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
		if (queryParams != null && !queryParams.isEmpty()) {
			String txnId = findValueByPrefix(queryParams, TransactionIdGenerator.TRANSACTION);
			if (txnId != null && !txnId.isBlank()) {
				return txnId;
			}
		}

		if (requestBody != null && !requestBody.isEmpty()) {
			String txnId = findValueByPrefix(requestBody, TransactionIdGenerator.TRANSACTION);
			if (txnId != null && !txnId.isBlank()) {
				return txnId;
			}
		}

		return null;
	}

	/**
	 * Recursively finds the first string value that starts with the given prefix in a map/list/array structure.
	 */
	private static String findValueByPrefix(Object obj, String prefix) {
		if (obj == null || prefix == null) {
			return null;
		}
		if (obj instanceof String str) {
			return str.startsWith(prefix) ? str : null;
		}
		if (obj instanceof Map<?, ?> map) {
			for (Object value : map.values()) {
				String result = findValueByPrefix(value, prefix);
				if (result != null) {
					return result;
				}
			}
		}
		if (obj instanceof List<?> list) {
			for (Object item : list) {
				String result = findValueByPrefix(item, prefix);
				if (result != null) {
					return result;
				}
			}
		}
		if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				String result = findValueByPrefix(Array.get(obj, i), prefix);
				if (result != null) {
					return result;
				}
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
