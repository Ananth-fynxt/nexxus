package fynxt.brand.health.service.impl;

import fynxt.brand.health.dto.HealthResponse;
import fynxt.brand.health.service.HealthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

	@Override
	public HealthResponse getHealthStatus() {
		return HealthResponse.healthy();
	}
}
