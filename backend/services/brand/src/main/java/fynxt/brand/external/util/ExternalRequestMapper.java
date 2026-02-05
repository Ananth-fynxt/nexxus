package fynxt.brand.external.util;

import fynxt.brand.shared.util.ValidationUtils;

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

		if (ValidationUtils.isNotNullOrEmpty(tnxId)) {
			externalDto.put("tnxId", tnxId);
		}

		if (ValidationUtils.isNotNullOrEmpty(requestBody)) {
			externalDto.put("body", requestBody);
		}

		if (ValidationUtils.isNotNullOrEmpty(queryParams)) {
			externalDto.put("query", queryParams);
		}

		if (ValidationUtils.isNotNullOrEmpty(headers)) {
			externalDto.put("headers", headers);
		}

		if (ValidationUtils.isNotNullOrEmpty(rawBody)) {
			externalDto.put("rawBody", rawBody);
		}

		return externalDto;
	}
}
