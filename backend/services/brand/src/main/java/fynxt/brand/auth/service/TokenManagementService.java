package fynxt.brand.auth.service;

import fynxt.jwt.dto.JwtTokenResponse;

public interface TokenManagementService {

	void saveToken(JwtTokenResponse tokenResponse, Integer customerId);

	void saveTokens(JwtTokenResponse accessToken, JwtTokenResponse refreshToken, Integer customerId);

	void revokeToken(String tokenHash);

	void revokeRefreshTokensForUser(String customerId);

	void revokeAllTokensForUser(String customerId);

	void revokeAccessTokensForUser(String customerId);

	boolean isRefreshTokenActive(String refreshToken, String subject);

	boolean isAccessTokenActive(String tokenHash);

	void updateExpiredTokenStatus(String tokenHash);

	void cleanupExpiredTokensForUser(Integer customerId);

	String generateTokenHashFromToken(String token, String subject);
}
