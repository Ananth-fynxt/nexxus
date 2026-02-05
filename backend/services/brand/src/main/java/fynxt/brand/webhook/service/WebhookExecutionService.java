package fynxt.brand.webhook.service;

import fynxt.brand.webhook.entity.WebhookLog;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WebhookExecutionService {

	void sendWebhook(
			UUID brandId,
			UUID environmentId,
			fynxt.brand.webhook.enums.WebhookStatusType statusType,
			Object payload,
			String correlationId);

	void sendWebhook(
			UUID brandId,
			UUID environmentId,
			fynxt.brand.webhook.enums.WebhookStatusType statusType,
			Object payload,
			String correlationId,
			String webhookId);

	void sendWebhookById(String webhookId, Object payload, String correlationId);

	Map<String, Object> getWebhookStats(String webhookId);

	List<WebhookLog> getWebhookLogs(String webhookId, int page, int size);
}
