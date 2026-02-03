package fynxt.brand.auth.service.impl;

import fynxt.auth.dto.AuthResponse;
import fynxt.auth.dto.LoginRequest;
import fynxt.auth.enums.AuthType;
import fynxt.auth.enums.TokenType;
import fynxt.auth.service.TokenValidationService;
import fynxt.brand.auth.dto.UserInfo;
import fynxt.brand.auth.service.AuthService;
import fynxt.brand.auth.service.TokenManagementService;
import fynxt.common.constants.ErrorCode;
import fynxt.jwt.dto.JwtTokenRequest;
import fynxt.jwt.dto.JwtTokenResponse;
import fynxt.jwt.dto.JwtValidationRequest;
import fynxt.jwt.dto.JwtValidationResponse;
import fynxt.jwt.exception.JwtSigningKeyException;
import fynxt.jwt.exception.JwtTokenGenerationException;
import fynxt.jwt.executor.JwtExecutor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${fynxt.jwt.issuer}")
	private String jwtIssuer;

	@Value("${fynxt.jwt.audience}")
	private String jwtAudience;

	@Value("${fynxt.jwt.signing-key-id}")
	private String signingKeyId;

	@Value("${fynxt.jwt.refresh-signing-key-id}")
	private String refreshSigningKeyId;

	@Value("${fynxt.jwt.access-token-expiration}")
	private Duration accessTokenExpiration;

	@Value("${fynxt.jwt.refresh-token-expiration}")
	private Duration refreshTokenExpiration;

	@Transactional
	public AuthResponse login(LoginRequest request) {

		var user = userAuthService.authenticateUser(request.getEmail(), request.getPassword());
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
		}

		try {
			JwtTokenResponse accessTokenResponse = generateUserToken(user);
			JwtTokenResponse refreshTokenResponse = generateUserRefreshToken(user);

			tokenManagementService.cleanupExpiredTokensForUser(user.getUserId());

			tokenManagementService.saveTokens(accessTokenResponse, refreshTokenResponse, user.getUserId());

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

	private JwtTokenResponse generateUserToken(UserInfo user) {
		Map<String, Object> claims = buildUserAccessTokenClaims(user);

		JwtTokenRequest jwtRequest = JwtTokenRequest.builder()
				.issuer(jwtIssuer)
				.audience(jwtAudience)
				.subject(user.getUserId().toString())
				.expiresAt(OffsetDateTime.now().plus(accessTokenExpiration))
				.claims(claims)
				.signingKeyId(signingKeyId)
				.tokenType(convertToExternalTokenType(TokenType.ACCESS))
				.build();

		return jwtExecutor.generateToken(jwtRequest);
	}

	private JwtTokenResponse generateUserRefreshToken(UserInfo user) {
		Map<String, Object> claims = buildUserRefreshTokenClaims(user);

		JwtTokenRequest jwtRequest = JwtTokenRequest.builder()
				.issuer(jwtIssuer)
				.audience(jwtAudience)
				.subject(user.getUserId().toString())
				.expiresAt(OffsetDateTime.now().plus(refreshTokenExpiration))
				.claims(claims)
				.signingKeyId(refreshSigningKeyId)
				.tokenType(convertToExternalTokenType(TokenType.REFRESH))
				.build();

		return jwtExecutor.generateToken(jwtRequest);
	}

	private Map<String, Object> buildUserAccessTokenClaims(UserInfo user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("auth_type", AuthType.APPLICATION_USER.getValue());
		claims.put("token_type", TokenType.ACCESS.getValue());
		claims.put("scope", user.getScope().getValue());
		claims.put("user_id", user.getUserId());
		claims.put("email", user.getEmail());
		if (user.getFiId() != null) {
			claims.put("fi_id", user.getFiId());
		}
		if (user.getFiName() != null) {
			claims.put("fi_name", user.getFiName());
		}
		if (user.getBrands() != null) {
			claims.put("brands", user.getBrands());
		}
		if (user.getAccessibleBrands() != null) {
			claims.put("accessible_brands", user.getAccessibleBrands());
		}
		return claims;
	}

	private Map<String, Object> buildUserRefreshTokenClaims(UserInfo user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("auth_type", AuthType.APPLICATION_USER.getValue());
		claims.put("token_type", TokenType.REFRESH.getValue());
		claims.put("user_id", user.getUserId());
		claims.put("scope", user.getScope().getValue());
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
		if (AuthType.APPLICATION_USER.getValue().equals(authType)) {
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
		UserInfo user = userAuthService.getUserInfoById(userId);
		return generateUserToken(user);
	}

	private JwtValidationResponse validateRefreshToken(String refreshToken) {
		JwtValidationRequest validationRequest = JwtValidationRequest.builder()
				.token(refreshToken)
				.issuer(jwtIssuer)
				.audience(jwtAudience)
				.signingKeyId(refreshSigningKeyId)
				.build();

		JwtValidationResponse validationResult = jwtExecutor.validateToken(validationRequest);
		if (!validationResult.isValid()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
		}

		String tokenType = (String) validationResult.getClaims().get("token_type");
		if (tokenType == null || !TokenType.REFRESH.getValue().equalsIgnoreCase(tokenType)) {
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
				.issuer(jwtIssuer)
				.audience(jwtAudience)
				.signingKeyId(signingKeyId)
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
}
