package fynxt.brand.brand.service;

import fynxt.brand.brand.dto.BrandDto;

import java.util.List;
import java.util.UUID;

public interface BrandService {

	BrandDto create(BrandDto brandDto);

	List<BrandDto> readAll();

	BrandDto read(UUID id);

	BrandDto update(BrandDto dto);

	void delete(UUID id);

	List<BrandDto> findByFiId(Short fiId);

	List<BrandDto> findByUserId(Integer userId);
}
