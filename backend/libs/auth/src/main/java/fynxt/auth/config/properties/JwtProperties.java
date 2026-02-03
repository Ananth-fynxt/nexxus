package fynxt.auth.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fynxt.jwt")
public record JwtProperties(
		String issuer,
		String audience,
		String signingKeyId,
		String refreshSigningKeyId,
		Duration accessTokenExpiration,
		Duration refreshTokenExpiration) {}
