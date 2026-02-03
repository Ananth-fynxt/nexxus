package fynxt.brand.auth.service;

import fynxt.auth.dto.AuthResponse;
import fynxt.auth.dto.LoginRequest;

public interface AuthService {

	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);

	String logout(String authorization);
}
