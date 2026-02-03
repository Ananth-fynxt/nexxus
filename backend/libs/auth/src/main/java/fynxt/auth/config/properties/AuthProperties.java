package fynxt.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fynxt.auth")
public record AuthProperties(String apiPrefix, String frontendUrl, String adminToken) {}
