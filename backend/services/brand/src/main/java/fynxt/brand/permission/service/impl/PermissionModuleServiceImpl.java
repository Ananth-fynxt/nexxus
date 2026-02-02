package fynxt.brand.permission.service.impl;

import fynxt.brand.permission.service.PermissionModuleService;
import fynxt.common.constants.ErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PermissionModuleServiceImpl implements PermissionModuleService {

	private final ObjectMapper objectMapper;

	@Override
	public Map<String, Object> getAvailableModules() {

		try {
			ClassPathResource resource = new ClassPathResource("static/permissions.json");
			InputStream inputStream = resource.getInputStream();

			@SuppressWarnings("unchecked")
			Map<String, Object> permissionsData = objectMapper.readValue(inputStream, Map.class);

			@SuppressWarnings("unchecked")
			Map<String, Object> availableModules = (Map<String, Object>) permissionsData.get("available_modules");

			if (availableModules == null || availableModules.isEmpty()) {
				throw new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_ERROR.getCode());
			}

			return availableModules;

		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERIC_ERROR.getCode());
		}
	}
}
