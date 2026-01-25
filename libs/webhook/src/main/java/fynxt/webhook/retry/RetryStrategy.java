package fynxt.webhook.retry;

import fynxt.webhook.dto.WebhookResponse;

public interface RetryStrategy {

	boolean shouldRetry(WebhookResponse response, int currentAttempt, int maxRetries);

	long calculateRetryDelaySeconds(int currentAttempt);
}
