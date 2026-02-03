package fynxt.brand.auth.service.impl;

import fynxt.auth.service.EnvironmentLookupService;
import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.service.EnvironmentService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandEnvironmentLookupServiceImpl implements EnvironmentLookupService {

	private final EnvironmentService environmentService;

	@Override
	public Map<String, Object> findBySecret(UUID secret) {
		try {
			EnvironmentDto environment = environmentService.findBySecret(secret);
			if (environment == null) {
				return null;
			}

			Map<String, Object> map = new HashMap<>();
			map.put("id", environment.getId());
			map.put("brandId", environment.getBrandId());
			map.put("name", environment.getName());
			return map;
		} catch (Exception e) {
			return null;
		}
	}
}
