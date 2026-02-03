package fynxt.brand.auth.filter;

import fynxt.brand.auth.context.BrandEnvironmentContext;
import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.brandrole.service.BrandRoleService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Order(100)
public class BrandEnvironmentContextFilter extends OncePerRequestFilter {

	private final BrandRoleService brandRoleService;

	@Override
	protected void doFilterInternal(
			@Nonnull HttpServletRequest request,
			@Nonnull HttpServletResponse response,
			@Nonnull FilterChain filterChain)
			throws ServletException, IOException {

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> claims = (Map<String, Object>) request.getAttribute("jwt.claims");
			String subject = (String) request.getAttribute("jwt.subject");

			if (claims != null && subject != null) {
				setBrandEnvironmentContext(claims, subject, request);
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> environment = (Map<String, Object>) request.getAttribute("environment");
			String authType = (String) request.getAttribute("auth_type");

			if (environment != null && authType != null) {
				setBrandEnvironmentContextFromSecret(environment, authType);
			}

			filterChain.doFilter(request, response);
		} finally {
			BrandEnvironmentContextHolder.clearContext();
		}
	}

	private void setBrandEnvironmentContext(Map<String, Object> claims, String subject, HttpServletRequest request) {
		String scope = toStringValue(claims.get("scope"));
		String authType = toStringValue(claims.get("auth_type"));
		Object fiIdObj = claims.get("fi_id");
		String customerId = (String) claims.get("customer_id");

		Short fiId = null;
		if (fiIdObj != null) {
			if (fiIdObj instanceof String) {
				fiId = Short.parseShort((String) fiIdObj);
			} else if (fiIdObj instanceof Integer) {
				fiId = ((Integer) fiIdObj).shortValue();
			} else if (fiIdObj instanceof Short) {
				fiId = (Short) fiIdObj;
			}
		}

		UUID brandId = readUuidHeader(request, "X-BRAND-ID");
		if (brandId == null) {
			brandId = parseUuid(claims.get("brand_id"));
		}

		UUID environmentId = readUuidHeader(request, "X-ENV-ID");
		if (environmentId == null) {
			environmentId = parseUuid(claims.get("environment_id"));
		}

		Integer roleId = resolveRoleId(claims, brandId, environmentId);
		Map<String, Object> rolePermissions = roleId != null ? brandRoleService.getRolePermissions(roleId) : null;

		List<UUID> accessibleBrandIds = extractAccessibleBrandIds(claims);

		BrandEnvironmentContext context = BrandEnvironmentContext.builder()
				.userId(Integer.parseInt(subject))
				.scope(scope)
				.authType(authType)
				.fiId(fiId)
				.customerId(customerId)
				.brandId(brandId)
				.environmentId(environmentId)
				.roleId(roleId)
				.rolePermissions(rolePermissions)
				.accessibleBrandIds(accessibleBrandIds)
				.build();

		BrandEnvironmentContextHolder.setContext(context);
	}

	private void setBrandEnvironmentContextFromSecret(Map<String, Object> environment, String authType) {
		UUID brandId = (UUID) environment.get("brandId");
		UUID environmentId = (UUID) environment.get("id");

		BrandEnvironmentContext context = BrandEnvironmentContext.builder()
				.brandId(brandId)
				.environmentId(environmentId)
				.authType(authType)
				.build();

		BrandEnvironmentContextHolder.setContext(context);
	}

	private Integer resolveRoleId(Map<String, Object> claims, UUID brandId, UUID environmentId) {
		if (brandId == null || environmentId == null) {
			return null;
		}

		Object accessibleBrandsObj = claims.get("accessible_brands");
		if (!(accessibleBrandsObj instanceof List)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<Object> accessibleBrands = (List<Object>) accessibleBrandsObj;
		for (Object brandObj : accessibleBrands) {
			if (!(brandObj instanceof Map)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> brandMap = (Map<String, Object>) brandObj;
			UUID brand = parseUuid(brandMap.get("id"));
			if (!brandId.equals(brand)) {
				continue;
			}

			Object environmentsObj = brandMap.get("environments");
			if (!(environmentsObj instanceof List)) {
				continue;
			}

			@SuppressWarnings("unchecked")
			List<Object> environments = (List<Object>) environmentsObj;
			for (Object envObj : environments) {
				if (!(envObj instanceof Map)) {
					continue;
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> envMap = (Map<String, Object>) envObj;
				UUID envId = parseUuid(envMap.get("id"));
				if (environmentId.equals(envId)) {
					return parseInteger(envMap.get("roleId"));
				}
			}
		}

		return null;
	}

	private List<UUID> extractAccessibleBrandIds(Map<String, Object> claims) {
		Object brandsObj = claims.get("accessible_brands");
		if (!(brandsObj instanceof List)) {
			brandsObj = claims.get("brands");
		}
		if (!(brandsObj instanceof List)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<Object> brands = (List<Object>) brandsObj;
		List<UUID> brandIds = new ArrayList<>();
		for (Object brandObj : brands) {
			if (!(brandObj instanceof Map)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> brandMap = (Map<String, Object>) brandObj;
			UUID brandId = parseUuid(brandMap.get("id"));
			if (brandId != null) {
				brandIds.add(brandId);
			}
		}
		return brandIds.isEmpty() ? null : brandIds;
	}

	private UUID readUuidHeader(HttpServletRequest request, String headerName) {
		String value = request.getHeader(headerName);
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private UUID parseUuid(Object value) {
		if (value instanceof UUID) {
			return (UUID) value;
		}
		if (value instanceof String) {
			try {
				return UUID.fromString((String) value);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

	private Integer parseInteger(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof Long) {
			return ((Long) value).intValue();
		}
		if (value instanceof Short) {
			return ((Short) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	private String toStringValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Enum<?>) {
			return ((Enum<?>) value).name();
		}
		return String.valueOf(value);
	}
}
