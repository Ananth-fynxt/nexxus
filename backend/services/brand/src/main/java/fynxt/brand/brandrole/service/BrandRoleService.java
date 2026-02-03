package fynxt.brand.brandrole.service;

import fynxt.brand.brandrole.dto.BrandRoleDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BrandRoleService {

	BrandRoleDto create(BrandRoleDto brandRoleDto);

	List<BrandRoleDto> readAll();

	List<BrandRoleDto> readAll(UUID brandId, UUID environmentId);

	BrandRoleDto read(Integer id);

	BrandRoleDto update(BrandRoleDto dto);

	void delete(Integer id);

	Map<String, Object> getRolePermissions(Integer roleId);
}
