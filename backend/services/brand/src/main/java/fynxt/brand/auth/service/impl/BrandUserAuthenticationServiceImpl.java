package fynxt.brand.auth.service.impl;

import fynxt.auth.service.UserAuthenticationService;
import fynxt.brand.brand.dto.BrandDto;
import fynxt.brand.brand.service.BrandService;
import fynxt.brand.branduser.dto.BrandUserDto;
import fynxt.brand.branduser.service.BrandUserService;
import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.service.EnvironmentService;
import fynxt.brand.fi.dto.FiDto;
import fynxt.brand.fi.service.FiService;
import fynxt.brand.user.entity.User;
import fynxt.brand.user.service.UserService;
import fynxt.common.enums.ErrorCode;
import fynxt.common.enums.Scope;
import fynxt.common.util.CryptoUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BrandUserAuthenticationServiceImpl implements UserAuthenticationService {

	private final UserService userService;
	private final FiService fiService;
	private final BrandService brandService;
	private final BrandUserService brandUserService;
	private final EnvironmentService environmentService;
	private final CryptoUtil cryptoUtil;

	@Override
	public Map<String, Object> authenticateUser(String email, String password) {
		User user = userService.findByEmailForAuthentication(email);
		validateUserPassword(user, password);
		return buildUserInfo(user);
	}

	@Override
	public Map<String, Object> getUserInfoById(Integer userId) {
		User user = userService.findByIdForAuthentication(userId);
		return buildUserInfo(user);
	}

	private Map<String, Object> buildUserInfo(User user) {
		try {
			FiDto fiDto = fiService.findByUserId(user.getId());
			return buildFiUserInfo(user, fiDto);
		} catch (ResponseStatusException e) {
			// Continue to brand user flow
		}

		List<BrandUserDto> brandUserDtos = brandUserService.findByUserId(user.getId());
		if (!brandUserDtos.isEmpty()) {
			return buildBrandUserInfo(user, brandUserDtos);
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.USER_NO_ACCESS.getCode());
	}

	private Map<String, Object> buildFiUserInfo(User user, FiDto fiDto) {
		List<BrandDto> brandDtos = brandService.findByFiId(fiDto.getId());
		List<Map<String, Object>> brandInfos = buildBrandInfoList(brandDtos, null);

		Map<String, Object> map = new HashMap<>();
		map.put("userId", user.getId());
		map.put("email", user.getEmail());
		map.put("scope", Scope.FI.name());
		map.put("status", fiDto.getStatus() != null ? fiDto.getStatus().name() : null);
		map.put("authType", "INTERNAL");
		map.put("fiId", fiDto.getId());
		map.put("fiName", fiDto.getName());
		map.put("brands", brandInfos);
		return map;
	}

	private Map<String, Object> buildBrandUserInfo(User user, List<BrandUserDto> brandUserDtos) {
		List<BrandDto> accessibleBrandDtos = brandService.findByUserId(user.getId());
		List<Map<String, Object>> brandInfos = buildBrandInfoList(accessibleBrandDtos, brandUserDtos);

		Map<String, Object> map = new HashMap<>();
		map.put("userId", user.getId());
		map.put("email", user.getEmail());
		map.put("scope", Scope.BRAND.name());
		map.put(
				"status",
				brandUserDtos.get(0).getStatus() != null
						? brandUserDtos.get(0).getStatus().name()
						: null);
		map.put("authType", "INTERNAL");
		map.put("accessibleBrands", brandInfos);
		return map;
	}

	private List<Map<String, Object>> buildBrandInfoList(List<BrandDto> brandDtos, List<BrandUserDto> brandUserDtos) {
		return brandDtos.stream()
				.map(brandDto -> buildBrandInfo(brandDto, brandUserDtos))
				.collect(Collectors.toList());
	}

	private Map<String, Object> buildBrandInfo(BrandDto brandDto, List<BrandUserDto> brandUserDtos) {
		List<EnvironmentDto> environments = environmentService.findByBrandId(brandDto.getId());
		List<Map<String, Object>> environmentInfos = environments.stream()
				.map(env -> buildEnvironmentInfo(env, brandDto.getId(), brandUserDtos))
				.collect(Collectors.toList());

		Map<String, Object> map = new HashMap<>();
		map.put("id", brandDto.getId());
		map.put("name", brandDto.getName());
		map.put("environments", environmentInfos);
		return map;
	}

	private Map<String, Object> buildEnvironmentInfo(
			EnvironmentDto environment, UUID brandId, List<BrandUserDto> brandUserDtos) {
		Integer roleId = null;

		if (brandUserDtos != null) {
			roleId = brandUserDtos.stream()
					.filter(bu -> bu.getBrandId().equals(brandId)
							&& bu.getEnvironmentId().equals(environment.getId()))
					.map(BrandUserDto::getBrandRoleId)
					.findFirst()
					.orElse(null);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("id", environment.getId());
		map.put("name", environment.getName());
		if (roleId != null) {
			map.put("roleId", roleId);
		}
		return map;
	}

	private void validateUserPassword(User user, String password) {
		try {
			String decryptedPassword = cryptoUtil.decrypt(user.getPassword());
			if (!password.equals(decryptedPassword)) {
				throw new ResponseStatusException(
						HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}
	}
}
