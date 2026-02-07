package fynxt.brand.external.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ExternalRequestMapper {

	public Map<String, Object> buildExternalDto(
			String token,
			String tnxId,
			String step,
			Map<String, Object> requestBody,
			Map<String, Object> queryParams,
			Map<String, Object> headers,
			String rawBody) {

		Map<String, Object> externalDto = new HashMap<>();
		externalDto.put("token", token);
		externalDto.put("step", step);

		if (tnxId != null && !tnxId.isBlank()) {
			externalDto.put("tnxId", tnxId);
		}

		if (requestBody != null && !requestBody.isEmpty()) {
			externalDto.put("body", requestBody);
		}

		if (queryParams != null && !queryParams.isEmpty()) {
			externalDto.put("query", queryParams);
		}

		if (headers != null && !headers.isEmpty()) {
			externalDto.put("headers", headers);
		}

		if (rawBody != null && !rawBody.isBlank()) {
			externalDto.put("rawBody", rawBody);
		}

		return externalDto;
	}
}
