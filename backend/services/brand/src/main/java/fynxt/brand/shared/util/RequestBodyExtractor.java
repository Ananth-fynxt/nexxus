package fynxt.brand.shared.util;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class RequestBodyExtractor {

	private final ObjectMapper objectMapper;

	public Map<String, Object> extractRequestBody(HttpServletRequest request) {
		try {
			String contentType = request.getContentType();
			if (contentType == null || contentType.isEmpty()) {
				return new HashMap<>();
			}

			if (contentType.contains("application/x-www-form-urlencoded")) {
				return extractFormData(request);
			}

			if (contentType.contains("application/json")) {
				return extractJsonData(request);
			}

			return new HashMap<>();
		} catch (Exception e) {
			return new HashMap<>();
		}
	}

	private Map<String, Object> extractFormData(HttpServletRequest request) {
		Map<String, Object> formData = new HashMap<>();
		request.getParameterMap().forEach((key, values) -> {
			if (values.length == 1) {
				formData.put(key, values[0]);
			} else {
				formData.put(key, values);
			}
		});
		return formData;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractJsonData(HttpServletRequest request) {
		try (BufferedReader reader = request.getReader()) {
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
			String jsonString = jsonBuilder.toString();
			if (jsonString.isEmpty()) {
				return new HashMap<>();
			}

			return objectMapper.readValue(jsonString, Map.class);
		} catch (Exception e) {
			return new HashMap<>();
		}
	}

	public String extractRawBody(HttpServletRequest request) {
		try {
			ContentCachingRequestWrapper wrapper =
					WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

			if (wrapper != null) {
				byte[] contentAsByteArray = wrapper.getContentAsByteArray();
				if (contentAsByteArray != null && contentAsByteArray.length > 0) {
					return new String(contentAsByteArray, StandardCharsets.UTF_8);
				}
			}

			StringBuilder bodyBuilder = new StringBuilder();
			try (BufferedReader reader = request.getReader()) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (bodyBuilder.length() > 0) {
						bodyBuilder.append("\n");
					}
					bodyBuilder.append(line);
				}
			}
			return bodyBuilder.toString();
		} catch (Exception e) {
			return "";
		}
	}
}
