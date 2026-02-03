package fynxt.brand.branduser.service.impl;

import fynxt.brand.brandrole.repository.BrandRoleRepository;
import fynxt.brand.branduser.dto.BrandUserDto;
import fynxt.brand.branduser.entity.BrandUser;
import fynxt.brand.branduser.repository.BrandUserRepository;
import fynxt.brand.branduser.service.BrandUserService;
import fynxt.brand.branduser.service.mappers.BrandUserMapper;
import fynxt.brand.user.dto.UserRequest;
import fynxt.brand.user.service.UserService;
import fynxt.common.constants.ErrorCode;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BrandUserServiceImpl implements BrandUserService {

	private final BrandUserRepository brandUserRepository;
	private final BrandUserMapper brandUserMapper;
	private final BrandRoleRepository brandRoleRepository;
	private final UserService userService;

	@Override
	@Transactional
	public BrandUserDto create(BrandUserDto dto) {
		verifyBrandRoleExists(dto.getBrandRoleId());
		verifyBrandUserEmailExists(dto.getBrandId(), dto.getEnvironmentId(), dto.getEmail());

		UserRequest createUserRequest =
				UserRequest.builder().email(dto.getEmail()).build();

		UserRequest createdUser = userService.createUser(createUserRequest);

		BrandUser brandUser = brandUserMapper.toBrandUser(dto);
		brandUser.setUserId(createdUser.getId());

		return brandUserMapper.toBrandUserDto(brandUserRepository.save(brandUser));
	}

	@Override
	public List<BrandUserDto> readAll() {
		return brandUserRepository.findAll().stream()
				.map(brandUserMapper::toBrandUserDto)
				.toList();
	}

	@Override
	public List<BrandUserDto> readAll(UUID brandId, UUID environmentId) {
		return brandUserRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
				.map(brandUserMapper::toBrandUserDto)
				.toList();
	}

	@Override
	public BrandUserDto read(Integer id) {
		BrandUser brandUser = brandUserRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode()));
		return brandUserMapper.toBrandUserDto(brandUser);
	}

	@Override
	@Transactional
	public BrandUserDto update(BrandUserDto dto) {
		verifyBrandRoleExists(dto.getBrandRoleId());
		Integer id = dto.getId();
		BrandUser existingBrandUser = brandUserRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode()));
		brandUserMapper.toUpdateBrandUser(dto, existingBrandUser);
		BrandUser brandUser = brandUserRepository.save(existingBrandUser);
		return brandUserMapper.toBrandUserDto(brandUser);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		BrandUser brandUser = brandUserRepository
				.findById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode()));

		brandUser.softDelete();
		brandUserRepository.save(brandUser);
	}

	private void verifyBrandRoleExists(Integer brandRoleId) {
		if (!brandRoleRepository.existsById(brandRoleId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode());
		}
	}

	private void verifyBrandUserEmailExists(UUID brandId, UUID environmentId, String email) {
		if (brandUserRepository.existsByBrandIdAndEnvironmentIdAndEmail(brandId, environmentId, email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.BRAND_USER_ALREADY_EXISTS.getCode());
		}
	}

	@Override
	public List<BrandUserDto> findByUserId(Integer userId) {
		List<BrandUser> brandUsers = brandUserRepository.findByUserId(userId);
		return brandUsers.stream().map(brandUserMapper::toBrandUserDto).toList();
	}

	@Override
	public boolean hasAccessToEnvironment(Integer userId, UUID brandId, UUID environmentId, Integer roleId) {
		return brandUserRepository.existsByUserIdAndBrandIdAndEnvironmentIdAndBrandRoleId(
				userId, brandId, environmentId, roleId);
	}
}
