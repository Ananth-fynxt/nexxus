package fynxt.brand.auth.filter;

import fynxt.brand.auth.context.BrandEnvironmentContext;
import fynxt.brand.auth.context.BrandEnvironmentContextHolder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(100)
public class BrandEnvironmentContextFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> claims = (Map<String, Object>) request.getAttribute("jwt.claims");
			String subject = (String) request.getAttribute("jwt.subject");

			if (claims != null && subject != null) {
				setBrandEnvironmentContext(claims, subject);
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

	private void setBrandEnvironmentContext(Map<String, Object> claims, String subject) {
		String scope = (String) claims.get("scope");
		String authType = (String) claims.get("auth_type");
		Object fiIdObj = claims.get("fi_id");
		String customerId = (String) claims.get("customer_id");
		Object brandIdObj = claims.get("brand_id");
		Object environmentIdObj = claims.get("environment_id");

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

		UUID brandId = null;
		if (brandIdObj instanceof String) {
			brandId = UUID.fromString((String) brandIdObj);
		}

		UUID environmentId = null;
		if (environmentIdObj instanceof String) {
			environmentId = UUID.fromString((String) environmentIdObj);
		}

		BrandEnvironmentContext context = BrandEnvironmentContext.builder()
				.userId(Integer.parseInt(subject))
				.scope(scope)
				.authType(authType)
				.fiId(fiId)
				.customerId(customerId)
				.brandId(brandId)
				.environmentId(environmentId)
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
}
