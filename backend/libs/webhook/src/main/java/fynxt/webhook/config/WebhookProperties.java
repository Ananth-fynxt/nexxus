package fynxt.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webhook")
public record WebhookProperties(
		Boolean enabled,
		Integer connectTimeoutMs,
		Integer readTimeoutMs,
		Integer maxRetries,
		Long baseDelaySeconds,
		Long maxRetryDelaySeconds,
		Double retryMultiplier) {}
