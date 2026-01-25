package fynxt.webhook.executor;

import fynxt.webhook.dto.WebhookRequest;
import fynxt.webhook.dto.WebhookResponse;

public interface WebhookExecutor {

	WebhookResponse execute(WebhookRequest request);
}
