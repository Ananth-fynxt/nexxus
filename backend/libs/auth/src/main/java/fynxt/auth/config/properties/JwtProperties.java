package fynxt.auth.config;

import java.time.Duration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fynxt.jwt")
public class JwtProperties {

	private String issuer;

	private String audience;

	private String signingKeyId;

	private String refreshSigningKeyId;

	private Duration accessTokenExpiration;

	private Duration refreshTokenExpiration;
}
