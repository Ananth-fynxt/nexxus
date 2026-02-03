package fynxt.auth.service;

public interface TokenValidationService {

	boolean isAccessTokenActive(String token, String subject);

	boolean isRefreshTokenActive(String refreshToken, String subject);

	default void updateExpiredTokenStatus(String token, String subject) {}
}
