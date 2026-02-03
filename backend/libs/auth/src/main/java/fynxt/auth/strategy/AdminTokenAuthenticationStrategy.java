package fynxt.auth.strategy;

import fynxt.auth.config.RouteConfig;
import fynxt.auth.filter.AuthenticationStrategy;
import fynxt.auth.util.ErrorResponseUtil;
import fynxt.common.constants.ErrorCode;

import java.io.IOException;
import java.util.List;

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
 * Authentication strategy for admin token-based authentication.
 * This strategy checks for X-ADMIN-TOKEN header and validates it against a configured admin token.
 * It has the highest priority (Order 0) to ensure admin requests are handled first.
 */
@Component
@Order(0) // Priority 0: Check X-ADMIN-TOKEN first (before X-SECRET-TOKEN and JWT)
@RequiredArgsConstructor
public class AdminTokenAuthenticationStrategy implements AuthenticationStrategy {

	private final RouteConfig routeConfig;

	private final AuthProperties authProperties;

	private static final String ADMIN_TOKEN_HEADER = "X-ADMIN-TOKEN";
	private static final String BYPASS_PERMISSION_ATTRIBUTE = "bypass.permission.check";

	@Override
	public boolean canHandle(HttpServletRequest request) {
		String requestUri = request.getRequestURI();

		if (!routeConfig.isAdminTokenPath(requestUri)) {
			return false;
		}

		String token = request.getHeader(ADMIN_TOKEN_HEADER);
		return StringUtils.isNotBlank(token);
	}

	@Override
	public boolean validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestUri = request.getRequestURI();

		if (!routeConfig.isAdminTokenPath(requestUri)) {
			return false;
		}

		String token = request.getHeader(ADMIN_TOKEN_HEADER);

		if (StringUtils.isBlank(token)) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
			return false;
		}

		if (StringUtils.isBlank(adminToken) || !adminToken.equals(token)) {
			ErrorResponseUtil.writeErrorResponse(request, response, ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
			return false;
		}

		request.setAttribute(BYPASS_PERMISSION_ATTRIBUTE, true);

		setSpringSecurityContext();

		return true;
	}

	private void setSpringSecurityContext() {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken("admin", null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
