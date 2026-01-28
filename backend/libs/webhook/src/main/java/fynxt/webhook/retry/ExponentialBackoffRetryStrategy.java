package fynxt.webhook.retry;

import fynxt.webhook.config.WebhookProperties;
import fynxt.webhook.constants.WebhookExecutionStatus;
import fynxt.webhook.dto.WebhookResponse;

public class ExponentialBackoffRetryStrategy implements RetryStrategy {

	private final long maxRetryDelaySeconds;
	private final double retryMultiplier;
	private final long baseDelaySeconds;

	public ExponentialBackoffRetryStrategy(WebhookProperties properties) {
		this.maxRetryDelaySeconds = properties.getMaxRetryDelaySeconds();
		this.retryMultiplier = properties.getRetryMultiplier();
		this.baseDelaySeconds = properties.getBaseDelaySeconds();
	}

	@Override
	public boolean shouldRetry(WebhookResponse response, int currentAttempt, int maxRetries) {
		if (currentAttempt >= maxRetries) {
			return false;
		}

		if (response.isSuccess()) {
			return false;
		}

		return WebhookExecutionStatus.FAILED.equals(response.getExecutionStatus());
	}

	@Override
	public long calculateRetryDelaySeconds(int currentAttempt) {
		long delay = (long) (baseDelaySeconds * Math.pow(retryMultiplier, currentAttempt - 1));
		return Math.min(delay, maxRetryDelaySeconds);
	}
}
