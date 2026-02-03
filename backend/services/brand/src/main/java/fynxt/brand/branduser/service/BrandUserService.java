package fynxt.brand.branduser.service;

import fynxt.brand.branduser.dto.BrandUserDto;

import java.util.List;
import java.util.UUID;

public interface BrandUserService {

	BrandUserDto create(BrandUserDto brandUserDto);

	List<BrandUserDto> readAll();

	List<BrandUserDto> readAll(UUID brandId, UUID environmentId);

	BrandUserDto read(Integer id);

	BrandUserDto update(BrandUserDto dto);

	void delete(Integer id);

	List<BrandUserDto> findByUserId(Integer userId);

	boolean hasAccessToEnvironment(Integer userId, UUID brandId, UUID environmentId, Integer roleId);
}
