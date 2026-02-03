package fynxt.auth.strategy;

import fynxt.auth.config.RouteConfig;
import fynxt.auth.enums.TokenType;
import fynxt.auth.filter.AuthenticationStrategy;
import fynxt.auth.service.TokenValidationService;
import fynxt.auth.util.ErrorResponseUtil;
import fynxt.common.constants.ErrorCode;
import fynxt.jwt.dto.JwtValidationRequest;
import fynxt.jwt.dto.JwtValidationResponse;
import fynxt.jwt.executor.JwtExecutor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
 * Authentication strategy for JWT-based authentication.
 * This strategy checks for Authorization Bearer header and validates the JWT token.
 * It has the lowest priority (Order 2) to ensure other authentication methods are tried first.
 */
@Component
@Order(2) // Priority 2: Check JWT only if X-SECRET-TOKEN and X-ADMIN-TOKEN are not present
@RequiredArgsConstructor
public class JwtAuthenticationStrategy implements AuthenticationStrategy {

	private final JwtExecutor jwtExecutor;
	private final RouteConfig routeConfig;
	private final TokenValidationService tokenValidationService;

	private final JwtProperties jwtProperties;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public boolean canHandle(HttpServletRequest request) {
		String secretToken = request.getHeader("X-SECRET-TOKEN");
		String adminToken = request.getHeader("X-ADMIN-TOKEN");

		if (StringUtils.isNotBlank(secretToken) || StringUtils.isNotBlank(adminToken)) {
			return false;
		}

		String requestUri = request.getRequestURI();

		if (!routeConfig.isJwtRequired(requestUri)) {
			return false;
		}

		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER_PREFIX);
	}

	@Override
	public boolean validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String secretToken = request.getHeader("X-SECRET-TOKEN");
		String adminToken = request.getHeader("X-ADMIN-TOKEN");

		if (StringUtils.isNotBlank(secretToken) || StringUtils.isNotBlank(adminToken)) {
			return false;
		}

		String requestUri = request.getRequestURI();

		if (!routeConfig.isJwtRequired(requestUri)) {
			return true;
		}

		String authHeader = request.getHeader(AUTHORIZATION_HEADER);

		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
			return false;
		}

		String token = authHeader.substring(BEARER_PREFIX.length()).trim();
		if (token.isEmpty()) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
			return false;
		}

		try {
			JwtValidationRequest validationRequest = JwtValidationRequest.builder()
					.token(token)
					.issuer(jwtIssuer)
					.audience(jwtAudience)
					.signingKeyId(signingKeyId)
					.build();

			JwtValidationResponse validationResult = jwtExecutor.validateToken(validationRequest);

			if (!validationResult.isValid()) {
				ErrorResponseUtil.writeErrorResponse(
						request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
				return false;
			}

			String tokenType = (String) validationResult.getClaims().get("token_type");
			if (tokenType == null || !TokenType.ACCESS.getValue().equalsIgnoreCase(tokenType)) {
				ErrorResponseUtil.writeErrorResponse(
						request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
				return false;
			}

			String subject = validationResult.getSubject();
			if (!isAccessTokenActiveInDatabase(token, subject)) {
				ErrorResponseUtil.writeErrorResponse(
						request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
				return false;
			}

			request.setAttribute("jwt.subject", validationResult.getSubject());
			request.setAttribute("jwt.claims", validationResult.getClaims());
			request.setAttribute("jwt.token", token);

			setSpringSecurityContext(validationResult.getClaims(), validationResult.getSubject());

			return true;
		} catch (Exception e) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
			return false;
		}
	}

	private void setSpringSecurityContext(Map<String, Object> claims, String subject) {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_AUTHENTICATED"));

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(subject, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private boolean isAccessTokenActiveInDatabase(String token, String subject) {
		try {
			boolean isActive = tokenValidationService.isAccessTokenActive(token, subject);

			if (!isActive) {
				tokenValidationService.updateExpiredTokenStatus(token, subject);
			}

			return isActive;
		} catch (Exception e) {
			return false;
		}
	}
}
