package fynxt.auth.strategy;

import fynxt.auth.config.RouteConfig;
import fynxt.auth.filter.AuthenticationStrategy;
import fynxt.auth.service.EnvironmentLookupService;
import fynxt.auth.util.ErrorResponseUtil;
import fynxt.common.constants.ErrorCode;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authentication strategy for secret token-based authentication.
 * This strategy checks for X-SECRET-TOKEN header and validates it by looking up the environment.
 * It has priority 1 (after admin token but before JWT).
 */
@Component
@Order(1) // Priority 1: Check X-SECRET-TOKEN after X-ADMIN-TOKEN but before JWT
@RequiredArgsConstructor
public class SecretTokenAuthenticationStrategy implements AuthenticationStrategy {

	private final EnvironmentLookupService environmentLookupService;
	private final RouteConfig routeConfig;

	private static final String SECRET_TOKEN_HEADER = "X-SECRET-TOKEN";
	private static final String BYPASS_PERMISSION_ATTRIBUTE = "bypass.permission.check";

	@Override
	public boolean canHandle(HttpServletRequest request) {
		String requestUri = request.getRequestURI();

		if (!routeConfig.isSecretTokenPath(requestUri)) {
			return false;
		}

		String token = request.getHeader(SECRET_TOKEN_HEADER);
		return StringUtils.isNotBlank(token);
	}

	@Override
	public boolean validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestUri = request.getRequestURI();

		if (!routeConfig.isSecretTokenPath(requestUri)) {
			return false;
		}

		String token = request.getHeader(SECRET_TOKEN_HEADER);

		if (StringUtils.isBlank(token)) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
			return false;
		}

		try {
			UUID secret;
			try {
				secret = UUID.fromString(token);
			} catch (IllegalArgumentException e) {
				ErrorResponseUtil.writeErrorResponse(
						request, response, ErrorCode.SECRET_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
				return false;
			}

			var environment = environmentLookupService.findBySecret(secret);

			if (environment == null) {
				ErrorResponseUtil.writeErrorResponse(
						request, response, ErrorCode.SECRET_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
				return false;
			}

			request.setAttribute("environment", environment);
			request.setAttribute("auth_type", "SECRET_TOKEN");

		} catch (Exception e) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
			return false;
		}

		request.setAttribute(BYPASS_PERMISSION_ATTRIBUTE, true);

		setSpringSecurityContext();

		return true;
	}

	private void setSpringSecurityContext() {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_SECRET"));

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken("secret", null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
