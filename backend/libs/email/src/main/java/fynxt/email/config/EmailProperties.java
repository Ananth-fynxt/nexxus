package fynxt.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email")
public record EmailProperties(Boolean enabled, String senderAddress, String connectionString, Integer threadPoolSize) {}
