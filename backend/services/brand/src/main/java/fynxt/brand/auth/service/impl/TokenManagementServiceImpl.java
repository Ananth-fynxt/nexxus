package fynxt.brand.auth.service.impl;

import fynxt.auth.enums.TokenStatus;
import fynxt.auth.enums.TokenType;
import fynxt.brand.auth.entity.Token;
import fynxt.brand.auth.repository.TokenRepository;
import fynxt.brand.auth.service.TokenManagementService;
import fynxt.common.enums.ErrorCode;
import fynxt.jwt.dto.JwtTokenResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TokenManagementServiceImpl implements TokenManagementService {

	private final TokenRepository tokenRepository;

	@Transactional
	public void saveToken(JwtTokenResponse tokenResponse, Integer customerId) {
		OffsetDateTime normalizedExpiresAt = tokenResponse
				.getExpiresAt()
				.withOffsetSameInstant(ZoneOffset.UTC)
				.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

		String tokenHash = generateTokenHash(tokenResponse.getToken(), customerId.toString(), normalizedExpiresAt);

		Token token = Token.builder()
				.customerId(customerId.toString())
				.tokenHash(tokenHash)
				.issuedAt(tokenResponse.getIssuedAt())
				.expiresAt(normalizedExpiresAt)
				.status(TokenStatus.ACTIVE)
				.tokenType(extractTokenType(tokenResponse.getClaims()))
				.build();

		tokenRepository.save(token);
	}

	@Transactional
	public void saveTokens(JwtTokenResponse accessToken, JwtTokenResponse refreshToken, Integer customerId) {
		OffsetDateTime normalizedAccessExpiresAt = extractExpirationFromToken(accessToken.getToken());
		if (normalizedAccessExpiresAt == null) {
			normalizedAccessExpiresAt = accessToken
					.getExpiresAt()
					.withOffsetSameInstant(ZoneOffset.UTC)
					.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
		}

		OffsetDateTime normalizedRefreshExpiresAt = extractExpirationFromToken(refreshToken.getToken());
		if (normalizedRefreshExpiresAt == null) {
			normalizedRefreshExpiresAt = refreshToken
					.getExpiresAt()
					.withOffsetSameInstant(ZoneOffset.UTC)
					.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
		}

		String accessTokenHash =
				generateTokenHash(accessToken.getToken(), customerId.toString(), normalizedAccessExpiresAt);

		String refreshTokenHash =
				generateTokenHash(refreshToken.getToken(), customerId.toString(), normalizedRefreshExpiresAt);

		Token accessTokenEntity = Token.builder()
				.customerId(customerId.toString())
				.tokenHash(accessTokenHash)
				.issuedAt(accessToken.getIssuedAt())
				.expiresAt(normalizedAccessExpiresAt)
				.status(TokenStatus.ACTIVE)
				.tokenType(TokenType.ACCESS)
				.build();

		Token refreshTokenEntity = Token.builder()
				.customerId(customerId.toString())
				.tokenHash(refreshTokenHash)
				.issuedAt(refreshToken.getIssuedAt())
				.expiresAt(normalizedRefreshExpiresAt)
				.status(TokenStatus.ACTIVE)
				.tokenType(TokenType.REFRESH)
				.build();

		tokenRepository.save(accessTokenEntity);
		tokenRepository.save(refreshTokenEntity);
	}

	private TokenType extractTokenType(java.util.Map<String, Object> claims) {
		String tokenTypeValue = (String) claims.get("token_type");
		if (tokenTypeValue != null) {
			String upperValue = tokenTypeValue.toUpperCase();
			if ("ACCESS".equals(upperValue)) {
				return TokenType.ACCESS;
			} else if ("REFRESH".equals(upperValue)) {
				return TokenType.REFRESH;
			}
		}
		return TokenType.ACCESS;
	}

	@Transactional
	public void revokeToken(String tokenHash) {
		tokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
			token.setStatus(TokenStatus.REVOKED);
			tokenRepository.save(token);
		});
	}

	private String generateTokenHash(String token, String customerId, OffsetDateTime expiresAt) {
		OffsetDateTime normalizedExpiresAt =
				expiresAt.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

		String expiresAtString =
				normalizedExpiresAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

		String data = String.format("%s|%s|%s", token, customerId, expiresAtString);
		String hash = hmacSha256(getBindingKey(), data);
		return hash;
	}

	private OffsetDateTime extractExpirationFromToken(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length == 3) {
				String payload = parts[1];
				while (payload.length() % 4 != 0) {
					payload += "=";
				}
				byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
				String payloadJson = new String(decodedBytes);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(payloadJson);
				long exp = jsonNode.get("exp").asLong();

				OffsetDateTime expiresAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneOffset.UTC);

				return expiresAt
						.withOffsetSameInstant(ZoneOffset.UTC)
						.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public String generateTokenHashFromToken(String token, String subject) {
		try {
			OffsetDateTime normalizedExpiresAt = extractExpirationFromToken(token);
			if (normalizedExpiresAt == null) {
				return null;
			}

			String hash = generateTokenHash(token, subject, normalizedExpiresAt);
			return hash;
		} catch (Exception e) {
			return null;
		}
	}

	public String getBindingKey() {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String keySource = "binding-key-fynxt-nexxus-token-system";
			byte[] keyBytes = digest.digest(keySource.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(keyBytes);
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_SECRET_GENERATION_FAILED.getCode());
		}
	}

	private String hmacSha256(String key, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hmacBytes);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate HMAC SHA-256", e);
		}
	}

	@Transactional
	public void revokeRefreshTokensForUser(String customerId) {
		tokenRepository
				.findActiveByCustomerIdAndTokenType(customerId, TokenType.REFRESH, TokenStatus.ACTIVE)
				.forEach(token -> {
					token.setStatus(TokenStatus.REVOKED);
					tokenRepository.save(token);
				});
	}

	@Transactional
	public void revokeAllTokensForUser(String customerId) {
		tokenRepository.findActiveByCustomerId(customerId, TokenStatus.ACTIVE).forEach(token -> {
			token.setStatus(TokenStatus.REVOKED);
			tokenRepository.save(token);
		});
	}

	@Transactional
	public void revokeAccessTokensForUser(String customerId) {
		tokenRepository
				.findActiveByCustomerIdAndTokenType(customerId, TokenType.ACCESS, TokenStatus.ACTIVE)
				.forEach(token -> {
					token.setStatus(TokenStatus.REVOKED);
					tokenRepository.save(token);
				});
	}

	public boolean isRefreshTokenActive(String refreshToken, String subject) {
		try {
			String refreshTokenHash = generateTokenHashFromToken(refreshToken, subject);
			if (refreshTokenHash == null) {
				return false;
			}

			Long count = tokenRepository.countActiveToken(refreshTokenHash, TokenStatus.ACTIVE, TokenType.REFRESH);

			return count != null && count > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAccessTokenActive(String tokenHash) {
		try {
			Long count = tokenRepository.countActiveToken(tokenHash, TokenStatus.ACTIVE, TokenType.ACCESS);

			return count != null && count > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional
	public void updateExpiredTokenStatus(String tokenHash) {
		try {
			Optional<Token> tokenOpt = tokenRepository.findByTokenHash(tokenHash);
			if (tokenOpt.isPresent()) {
				Token token = tokenOpt.get();

				if (token.getStatus() == TokenStatus.ACTIVE
						&& token.getExpiresAt().isBefore(OffsetDateTime.now())) {

					token.setStatus(TokenStatus.EXPIRED);
					tokenRepository.save(token);
				}
			}
		} catch (Exception e) {
			// Silently fail
		}
	}

	@Transactional
	public void cleanupExpiredTokensForUser(Integer customerId) {
		try {
			OffsetDateTime now = OffsetDateTime.now();
			List<Token> expiredTokens = tokenRepository.findExpiredTokens(now, TokenStatus.ACTIVE).stream()
					.filter(token -> customerId.toString().equals(token.getCustomerId()))
					.toList();

			if (!expiredTokens.isEmpty()) {
				expiredTokens.forEach(token -> {
					token.setStatus(TokenStatus.EXPIRED);
					tokenRepository.save(token);
				});
			}
		} catch (Exception e) {
			// Silently fail
		}
	}
}
