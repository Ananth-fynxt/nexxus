package fynxt.brand.brandrole.service.impl;

import fynxt.brand.brandrole.dto.BrandRoleDto;
import fynxt.brand.brandrole.entity.BrandRole;
import fynxt.brand.brandrole.repository.BrandRoleRepository;
import fynxt.brand.brandrole.service.BrandRoleService;
import fynxt.brand.brandrole.service.mappers.BrandRoleMapper;
import fynxt.common.constants.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandRoleServiceImpl implements BrandRoleService {

	private final BrandRoleRepository brandRoleRepository;
	private final BrandRoleMapper brandRoleMapper;
	private final ObjectMapper objectMapper;

	@Override
	@Transactional
	public BrandRoleDto create(BrandRoleDto dto) {
		verifyBrandRoleNameExistsForBrandAndEnvironment(dto.getBrandId(), dto.getEnvironmentId(), dto.getName());
		BrandRole brandRole = brandRoleMapper.toBrandRole(dto);
		return brandRoleMapper.toBrandRoleDto(brandRoleRepository.save(brandRole));
	}

	@Override
	public List<BrandRoleDto> readAll() {
		return brandRoleRepository.findAll().stream()
				.map(brandRoleMapper::toBrandRoleDto)
				.toList();
	}

	@Override
	public List<BrandRoleDto> readAll(UUID brandId, UUID environmentId) {
		return brandRoleRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
				.map(brandRoleMapper::toBrandRoleDto)
				.toList();
	}

	@Override
	public BrandRoleDto read(Integer id) {
		BrandRole brandRole = brandRoleRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode()));
		return brandRoleMapper.toBrandRoleDto(brandRole);
	}

	@Override
	@Transactional
	public BrandRoleDto update(BrandRoleDto dto) {
		Integer id = dto.getId();
		BrandRole existingBrandRole = brandRoleRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode()));
		brandRoleMapper.toUpdateBrandRole(dto, existingBrandRole);
		BrandRole brandRole = brandRoleRepository.save(existingBrandRole);
		return brandRoleMapper.toBrandRoleDto(brandRole);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		BrandRole brandRole = brandRoleRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode()));

		brandRole.softDelete();
		brandRoleRepository.save(brandRole);
	}

	@Override
	public Map<String, Object> getRolePermissions(Integer roleId) {
		try {
			return brandRoleRepository
					.findById(roleId)
					.map(BrandRole::getPermission)
					.filter(permission -> permission != null)
					.map(permission -> permission.toString().trim())
					.filter(json -> !json.isEmpty())
					.map(json -> {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> result = objectMapper.readValue(json, Map.class);
							return result;
						} catch (Exception e) {
							return null;
						}
					})
					.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	private void verifyBrandRoleNameExistsForBrandAndEnvironment(UUID brandId, UUID environmentId, String name) {
		if (brandRoleRepository.existsByBrandIdAndEnvironmentIdAndName(brandId, environmentId, name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.BRAND_ROLE_ALREADY_EXISTS.getCode());
		}
	}
}
