package fynxt.webhook.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "webhook")
public class WebhookProperties {

	private boolean enabled = false;
	private int connectTimeoutMs = 5000;
	private int readTimeoutMs = 30000;
	private int maxRetries = 3;
	private long baseDelaySeconds = 10;
	private long maxRetryDelaySeconds = 3600;
	private double retryMultiplier = 2.0;
}
