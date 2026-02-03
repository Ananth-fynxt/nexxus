package fynxt.brand.auth.service.impl;

import fynxt.auth.service.TokenValidationService;
import fynxt.brand.auth.service.TokenManagementService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

	private final TokenManagementService tokenManagementService;

	@Override
	public boolean isAccessTokenActive(String token, String subject) {
		try {
			String tokenHash = tokenManagementService.generateTokenHashFromToken(token, subject);
			if (tokenHash == null) {
				return false;
			}
			return tokenManagementService.isAccessTokenActive(tokenHash);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isRefreshTokenActive(String refreshToken, String subject) {
		return tokenManagementService.isRefreshTokenActive(refreshToken, subject);
	}

	@Override
	public void updateExpiredTokenStatus(String token, String subject) {
		try {
			String tokenHash = tokenManagementService.generateTokenHashFromToken(token, subject);
			if (tokenHash != null) {
				tokenManagementService.updateExpiredTokenStatus(tokenHash);
			}
		} catch (Exception e) {
			// Silently fail
		}
	}
}
