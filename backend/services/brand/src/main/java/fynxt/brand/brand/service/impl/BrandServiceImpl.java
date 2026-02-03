package fynxt.brand.brand.service.impl;

import fynxt.brand.brand.dto.BrandDto;
import fynxt.brand.brand.entity.Brand;
import fynxt.brand.brand.repository.BrandRepository;
import fynxt.brand.brand.service.BrandService;
import fynxt.brand.brand.service.mappers.BrandMapper;
import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.service.EnvironmentService;
import fynxt.brand.fi.repository.FiRepository;
import fynxt.common.enums.ErrorCode;
import fynxt.common.service.NameUniquenessService;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

	private final BrandRepository brandRepository;
	private final BrandMapper brandMapper;
	private final FiRepository fiRepository;
	private final EnvironmentService environmentService;
	private final NameUniquenessService nameUniquenessService;

	@Override
	@Transactional
	public BrandDto create(BrandDto dto) {
		if (dto.getFiId() != null) {
			verifyFiExists(dto.getFiId());
		}
		nameUniquenessService.validateForCreate(
				name -> brandRepository.existsByFiIdAndName(dto.getFiId(), name), "Brand", dto.getName());

		if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
			if (brandRepository.existsByEmail(dto.getEmail())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE.getCode());
			}
		}

		Brand brand = brandMapper.toBrand(dto);
		Brand savedBrand = brandRepository.save(brand);

		EnvironmentDto createdEnvironment = createDefaultEnvironments(savedBrand.getId());
		BrandDto brandDto = brandMapper.toBrandDto(savedBrand);

		if (createdEnvironment != null && createdEnvironment.getSecret() != null) {
			BrandDto.EnvironmentInfo environmentInfo = BrandDto.EnvironmentInfo.builder()
					.id(createdEnvironment.getId())
					.apiKey(createdEnvironment.getSecret())
					.name(createdEnvironment.getName())
					.build();
			brandDto.setEnvironments(List.of(environmentInfo));
		}

		return brandDto;
	}

	@Override
	public List<BrandDto> readAll() {
		return brandRepository.findAll().stream().map(brandMapper::toBrandDto).toList();
	}

	@Override
	public BrandDto read(UUID id) {
		Brand brand = brandRepository
				.findById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode()));
		return brandMapper.toBrandDto(brand);
	}

	@Override
	@Transactional
	public BrandDto update(BrandDto dto) {
		Brand existingBrand = brandRepository
				.findById(dto.getId())
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode()));

		if (dto.getFiId() != null) {
			verifyFiExists(dto.getFiId());
		}

		if (!existingBrand.getName().equals(dto.getName())) {
			if (brandRepository.existsByFiIdAndNameAndIdNot(dto.getFiId(), dto.getName(), dto.getId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Brand name already exists for this FI");
			}
		}

		brandMapper.toUpdateBrand(dto, existingBrand);
		Brand brand = brandRepository.save(existingBrand);
		return brandMapper.toBrandDto(brand);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Brand brand = brandRepository
				.findById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode()));
		brand.softDelete();
		brandRepository.save(brand);
	}

	private void verifyFiExists(Short fiId) {
		if (!fiRepository.existsById(fiId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FI_NOT_FOUND.getCode());
		}
	}

	private EnvironmentDto createDefaultEnvironments(UUID brandId) {
		try {
			EnvironmentDto productionEnvironment =
					EnvironmentDto.builder().name("Production").brandId(brandId).build();
			EnvironmentDto createdEnvironment = environmentService.create(productionEnvironment);
			return createdEnvironment;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<BrandDto> findByFiId(Short fiId) {
		List<Brand> brands = brandRepository.findByFiId(fiId);
		return brands.stream().map(brandMapper::toBrandDto).toList();
	}

	@Override
	public List<BrandDto> findByUserId(Integer userId) {
		List<Brand> brands = brandRepository.findByUserId(userId);
		return brands.stream().map(brandMapper::toBrandDto).toList();
	}
}
