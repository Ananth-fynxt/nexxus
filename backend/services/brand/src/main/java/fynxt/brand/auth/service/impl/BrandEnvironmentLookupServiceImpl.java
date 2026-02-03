package fynxt.brand.auth.service.impl;

import fynxt.auth.service.EnvironmentLookupService;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BrandEnvironmentLookupServiceImpl implements EnvironmentLookupService {

	@Override
	public Map<String, Object> findBySecret(UUID secret) {
		throw new ResponseStatusException(
				HttpStatus.NOT_IMPLEMENTED,
				"Environment lookup not implemented. Please implement this method with your environment service.");
	}
}
