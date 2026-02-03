package fynxt.auth.filter;

import fynxt.auth.config.RouteConfig;
import fynxt.auth.util.ErrorResponseUtil;
import fynxt.common.constants.ErrorCode;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AccessTokenOncePerRequestFilter extends OncePerRequestFilter {

	private final RouteConfig routeConfig;
	private final List<AuthenticationStrategy> authenticationStrategies;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String requestUri = request.getRequestURI();

		if (routeConfig.isPublic(requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		AuthenticationStrategy strategy = findAuthenticationStrategy(request);
		if (strategy == null) {
			ErrorResponseUtil.writeErrorResponse(
					request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
			return;
		}

		if (!strategy.validate(request, response)) {
			return;
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Find the first authentication strategy that can handle the request.
	 * Strategies are ordered by @Order annotation:
	 * - Order 0: AdminTokenAuthenticationStrategy (X-ADMIN-TOKEN)
	 * - Order 1: SecretTokenAuthenticationStrategy (X-SECRET-TOKEN)
	 * - Order 2: JwtAuthenticationStrategy (JWT Bearer token)
	 */
	private AuthenticationStrategy findAuthenticationStrategy(HttpServletRequest request) {
		return authenticationStrategies.stream()
				.filter(strategy -> strategy.canHandle(request))
				.findFirst()
				.orElse(null);
	}
}
