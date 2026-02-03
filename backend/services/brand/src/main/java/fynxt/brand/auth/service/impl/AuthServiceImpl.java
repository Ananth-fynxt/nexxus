package fynxt.brand.auth.service.impl;

import fynxt.auth.config.properties.JwtProperties;
import fynxt.auth.dto.AuthResponse;
import fynxt.auth.dto.LoginRequest;
import fynxt.auth.enums.AuthType;
import fynxt.auth.enums.TokenType;
import fynxt.auth.service.TokenValidationService;
import fynxt.brand.auth.service.AuthService;
import fynxt.brand.auth.service.TokenManagementService;
import fynxt.common.enums.ErrorCode;
import fynxt.jwt.dto.JwtTokenRequest;
import fynxt.jwt.dto.JwtTokenResponse;
import fynxt.jwt.dto.JwtValidationRequest;
import fynxt.jwt.dto.JwtValidationResponse;
import fynxt.jwt.exception.JwtSigningKeyException;
import fynxt.jwt.exception.JwtTokenGenerationException;
import fynxt.jwt.executor.JwtExecutor;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtExecutor jwtExecutor;
	private final fynxt.auth.service.UserAuthenticationService userAuthService;
	private final TokenValidationService tokenValidationService;
	private final TokenManagementService tokenManagementService;
	private final JwtProperties jwtProperties;

	@Transactional
	public AuthResponse login(LoginRequest request) {

		Map<String, Object> user = userAuthService.authenticateUser(request.getEmail(), request.getPassword());
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}
		Integer userId = extractUserId(user);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}

		try {
			JwtTokenResponse accessTokenResponse = generateUserToken(user);
			JwtTokenResponse refreshTokenResponse = generateUserRefreshToken(user);

			tokenManagementService.cleanupExpiredTokensForUser(userId);

			tokenManagementService.saveTokens(accessTokenResponse, refreshTokenResponse, userId);

			return buildAuthResponse(accessTokenResponse, refreshTokenResponse.getToken());
		} catch (JwtTokenGenerationException | JwtSigningKeyException e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_ISSUANCE_FAILED.getCode());
		}
	}

	@Transactional
	public AuthResponse refreshToken(String refreshToken) {

		try {
			JwtValidationResponse validationResult = validateRefreshToken(refreshToken);
			String authType = (String) validationResult.getClaims().get("auth_type");
			String subject = validationResult.getSubject();
			Integer userId = Integer.parseInt(subject);

			JwtTokenResponse newTokenResponse = generateAccessTokenFromRefreshToken(authType, validationResult);

			tokenManagementService.revokeAccessTokensForUser(subject);
			tokenManagementService.saveToken(newTokenResponse, userId);

			return buildAuthResponse(newTokenResponse, refreshToken);
		} catch (JwtTokenGenerationException | JwtSigningKeyException e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}
	}

	@Transactional
	public String logout(String authorization) {
		String token = authorization.replace("Bearer ", "");
		String userId = extractUserId(token);
		if (userId != null) {
			tokenManagementService.revokeAllTokensForUser(userId);
			return "Logout successful";
		} else {
			return "Logout failed";
		}
	}

	private JwtTokenResponse generateUserToken(Map<String, Object> user) {
		Integer userId = extractUserId(user);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}
		Map<String, Object> claims = buildUserAccessTokenClaims(user);

		JwtTokenRequest jwtRequest = JwtTokenRequest.builder()
				.issuer(jwtProperties.issuer())
				.audience(jwtProperties.audience())
				.subject(userId.toString())
				.expiresAt(OffsetDateTime.now().plus(jwtProperties.accessTokenExpiration()))
				.claims(claims)
				.signingKeyId(jwtProperties.signingKeyId())
				.tokenType(convertToExternalTokenType(TokenType.ACCESS))
				.build();

		return jwtExecutor.generateToken(jwtRequest);
	}

	private JwtTokenResponse generateUserRefreshToken(Map<String, Object> user) {
		Integer userId = extractUserId(user);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}
		Map<String, Object> claims = buildUserRefreshTokenClaims(user);

		JwtTokenRequest jwtRequest = JwtTokenRequest.builder()
				.issuer(jwtProperties.issuer())
				.audience(jwtProperties.audience())
				.subject(userId.toString())
				.expiresAt(OffsetDateTime.now().plus(jwtProperties.refreshTokenExpiration()))
				.claims(claims)
				.signingKeyId(jwtProperties.refreshSigningKeyId())
				.tokenType(convertToExternalTokenType(TokenType.REFRESH))
				.build();

		return jwtExecutor.generateToken(jwtRequest);
	}

	private Map<String, Object> buildUserAccessTokenClaims(Map<String, Object> user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("auth_type", AuthType.APPLICATION_USER.name());
		claims.put("token_type", TokenType.ACCESS.name());
		claims.put("scope", toStringValue(user.get("scope")));
		claims.put("user_id", extractUserId(user));
		claims.put("email", user.get("email"));
		if (user.get("fiId") != null) {
			claims.put("fi_id", user.get("fiId"));
		}
		if (user.get("fiName") != null) {
			claims.put("fi_name", user.get("fiName"));
		}
		if (user.get("brands") != null) {
			claims.put("brands", user.get("brands"));
		}
		if (user.get("accessibleBrands") != null) {
			claims.put("accessible_brands", user.get("accessibleBrands"));
		}
		return claims;
	}

	private Map<String, Object> buildUserRefreshTokenClaims(Map<String, Object> user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("auth_type", AuthType.APPLICATION_USER.name());
		claims.put("token_type", TokenType.REFRESH.name());
		claims.put("user_id", extractUserId(user));
		claims.put("scope", toStringValue(user.get("scope")));
		return claims;
	}

	private fynxt.jwt.enums.TokenType convertToExternalTokenType(TokenType localType) {
		return switch (localType) {
			case ACCESS -> fynxt.jwt.enums.TokenType.ACCESS;
			case REFRESH -> fynxt.jwt.enums.TokenType.REFRESH;
		};
	}

	private JwtTokenResponse generateAccessTokenFromRefreshToken(
			String authType, JwtValidationResponse validationResult) {
		if (AuthType.APPLICATION_USER.name().equals(authType)) {
			return generateAccessTokenForApplicationUser(validationResult);
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}
	}

	private JwtTokenResponse generateAccessTokenForApplicationUser(JwtValidationResponse validationResult) {
		Object userIdObj = validationResult.getClaims().get("user_id");
		Integer userId;
		if (userIdObj instanceof Integer) {
			userId = (Integer) userIdObj;
		} else if (userIdObj instanceof String) {
			userId = Integer.parseInt((String) userIdObj);
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}
		Map<String, Object> user = userAuthService.getUserInfoById(userId);
		return generateUserToken(user);
	}

	private JwtValidationResponse validateRefreshToken(String refreshToken) {
		JwtValidationRequest validationRequest = JwtValidationRequest.builder()
				.token(refreshToken)
				.issuer(jwtProperties.issuer())
				.audience(jwtProperties.audience())
				.signingKeyId(jwtProperties.refreshSigningKeyId())
				.build();

		JwtValidationResponse validationResult = jwtExecutor.validateToken(validationRequest);
		if (!validationResult.isValid()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}

		String tokenType = (String) validationResult.getClaims().get("token_type");
		if (tokenType == null || !TokenType.REFRESH.name().equalsIgnoreCase(tokenType)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}

		String subject = validationResult.getSubject();
		if (!tokenValidationService.isRefreshTokenActive(refreshToken, subject)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}

		return validationResult;
	}

	private String extractUserId(String token) {
		JwtValidationRequest request = JwtValidationRequest.builder()
				.token(token)
				.issuer(jwtProperties.issuer())
				.audience(jwtProperties.audience())
				.signingKeyId(jwtProperties.signingKeyId())
				.build();

		JwtValidationResponse result = jwtExecutor.validateToken(request);
		if (result.isValid()) {
			Object userIdObj = result.getClaims().get("user_id");
			if (userIdObj != null) {
				if (userIdObj instanceof Integer) {
					return String.valueOf((Integer) userIdObj);
				} else if (userIdObj instanceof String) {
					return (String) userIdObj;
				}
			}
		}
		return null;
	}

	private AuthResponse buildAuthResponse(JwtTokenResponse accessTokenResponse, String refreshToken) {

		Map<String, Object> claims = new HashMap<>();
		claims.putAll(accessTokenResponse.getClaims());

		return AuthResponse.builder()
				.accessToken(accessTokenResponse.getToken())
				.refreshToken(refreshToken)
				.tokenType(accessTokenResponse.getTokenType())
				.issuedAt(accessTokenResponse.getIssuedAt())
				.expiresAt(accessTokenResponse.getExpiresAt())
				.claims(claims)
				.build();
	}

	private Integer extractUserId(Map<String, Object> user) {
		Object userIdObj = user.get("userId");
		if (userIdObj instanceof Integer) {
			return (Integer) userIdObj;
		}
		if (userIdObj instanceof Long) {
			return ((Long) userIdObj).intValue();
		}
		if (userIdObj instanceof String) {
			try {
				return Integer.parseInt((String) userIdObj);
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
