package fynxt.brand.environment.service.impl;

import fynxt.brand.environment.dto.EnvironmentCredentialsDto;
import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.entity.Environment;
import fynxt.brand.environment.repository.EnvironmentRepository;
import fynxt.brand.environment.service.EnvironmentService;
import fynxt.brand.environment.service.mappers.EnvironmentMapper;
import fynxt.common.enums.ErrorCode;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

	private final EnvironmentRepository environmentRepository;
	private final EnvironmentMapper environmentMapper;

	@Override
	@Transactional
	public EnvironmentDto create(EnvironmentDto dto) {
		verifyEnvironmentNameExistsForBrand(dto.getBrandId(), dto.getName());

		Environment environment = environmentMapper.toEnvironment(dto);
		environment.setSecret(UUID.randomUUID());
		environment.setToken(UUID.randomUUID());

		Environment savedEnvironment = environmentRepository.save(environment);
		return environmentMapper.toEnvironmentDto(savedEnvironment);
	}

	@Override
	public List<EnvironmentDto> readAll() {
		return environmentRepository.findAll().stream()
				.map(environmentMapper::toEnvironmentDto)
				.map(this::sanitizeSecrets)
				.toList();
	}

	@Override
	public EnvironmentDto read(UUID id) {
		Environment environment = environmentRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
		return sanitizeSecrets(environmentMapper.toEnvironmentDto(environment));
	}

	@Override
	@Transactional
	public EnvironmentDto update(EnvironmentDto dto) {
		Environment existingEnvironment = environmentRepository
				.findById(dto.getId())
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
		environmentMapper.toUpdateEnvironment(dto, existingEnvironment);
		Environment environment = environmentRepository.save(existingEnvironment);
		return sanitizeSecrets(environmentMapper.toEnvironmentDto(environment));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Environment environment = environmentRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
		environment.softDelete();
		environmentRepository.save(environment);
	}

	private void verifyEnvironmentNameExistsForBrand(UUID brandId, String name) {
		if (environmentRepository.existsByBrandIdAndName(brandId, name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.ENVIRONMENT_ALREADY_EXISTS.getCode());
		}
	}

	@Override
	public List<EnvironmentDto> findByBrandId(UUID brandId) {
		return environmentRepository.findByBrandId(brandId).stream()
				.map(environmentMapper::toEnvironmentDto)
				.map(this::sanitizeSecrets)
				.toList();
	}

	@Override
	@Transactional
	public EnvironmentDto rotateSecret(UUID id) {
		Environment environment = environmentRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));

		environment.setSecret(UUID.randomUUID());
		environment.setToken(UUID.randomUUID());

		Environment savedEnvironment = environmentRepository.save(environment);
		return environmentMapper.toEnvironmentDto(savedEnvironment);
	}

	@Override
	public EnvironmentDto readByToken(UUID token) {
		Environment environment = environmentRepository
				.findByToken(token)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
		return environmentMapper.toEnvironmentDto(environment);
	}

	@Override
	public EnvironmentDto readBySecret(UUID secret) {
		Environment environment = environmentRepository
				.findBySecret(secret)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
		return environmentMapper.toEnvironmentDto(environment);
	}

	@Override
	public EnvironmentDto findBySecret(UUID secret) {
		return readBySecret(secret);
	}

	@Override
	public EnvironmentCredentialsDto readCredentials(UUID id) {
		return environmentRepository
				.findCredentialsById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
	}

	private EnvironmentDto sanitizeSecrets(EnvironmentDto dto) {
		dto.setSecret(null);
		dto.setToken(null);
		return dto;
	}
}
