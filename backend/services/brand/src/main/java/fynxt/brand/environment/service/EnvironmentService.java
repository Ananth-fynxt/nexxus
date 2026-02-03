package fynxt.brand.environment.service;

import fynxt.brand.environment.dto.EnvironmentCredentialsDto;
import fynxt.brand.environment.dto.EnvironmentDto;

import java.util.List;
import java.util.UUID;

public interface EnvironmentService {

	EnvironmentDto create(EnvironmentDto environmentDto);

	List<EnvironmentDto> readAll();

	EnvironmentDto read(UUID id);

	EnvironmentDto update(EnvironmentDto dto);

	void delete(UUID id);

	List<EnvironmentDto> findByBrandId(UUID brandId);

	EnvironmentDto rotateSecret(UUID id);

	EnvironmentDto readByToken(UUID token);

	EnvironmentDto findBySecret(UUID secret);

	EnvironmentDto readBySecret(UUID secret);

	EnvironmentCredentialsDto readCredentials(UUID id);
}
