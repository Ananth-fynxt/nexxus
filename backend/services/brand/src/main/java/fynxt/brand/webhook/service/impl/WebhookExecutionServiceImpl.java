package fynxt.brand.webhook.service.impl;

import fynxt.brand.webhook.entity.Webhook;
import fynxt.brand.webhook.entity.WebhookLog;
import fynxt.brand.webhook.enums.WebhookExecutionStatus;
import fynxt.brand.webhook.repository.WebhookLogRepository;
import fynxt.brand.webhook.repository.WebhookRepository;
import fynxt.brand.webhook.service.WebhookExecutionService;
import fynxt.common.enums.Status;
import fynxt.webhook.config.WebhookProperties;
import fynxt.webhook.dto.WebhookRequest;
import fynxt.webhook.dto.WebhookResponse;
import fynxt.webhook.executor.WebhookExecutor;
import fynxt.webhook.retry.RetryStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookExecutionServiceImpl implements WebhookExecutionService {

	private final WebhookRepository webhookRepository;
	private final WebhookLogRepository webhookLogRepository;
	private final JobScheduler jobScheduler;
	private final WebhookExecutor webhookExecutor;
	private final RetryStrategy retryStrategy;
	private final ObjectMapper webhookObjectMapper;
	private final WebhookProperties webhookProperties;

	@Override
	public void sendWebhook(
			UUID brandId,
			UUID environmentId,
			fynxt.brand.webhook.enums.WebhookStatusType statusType,
			Object payload,
			String correlationId) {
		sendWebhook(brandId, environmentId, statusType, payload, correlationId, null);
	}

	@Override
	public void sendWebhook(
			UUID brandId,
			UUID environmentId,
			fynxt.brand.webhook.enums.WebhookStatusType statusType,
			Object payload,
			String correlationId,
			String webhookId) {

		List<Webhook> webhooks = findActiveWebhooks(brandId, environmentId, statusType, webhookId);

		for (Webhook webhook : webhooks) {
			try {
				enqueueWebhookJob(webhook, payload, correlationId, 0);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void sendWebhookById(String webhookId, Object payload, String correlationId) {
		Short id = Short.parseShort(webhookId);
		Optional<Webhook> webhookOpt = webhookRepository.findById(id);

		if (webhookOpt.isEmpty()) {
			return;
		}

		Webhook webhook = webhookOpt.get();
		if (webhook.getStatus() != Status.ENABLED) {
			return;
		}

		enqueueWebhookJob(webhook, payload, correlationId, 0);
	}

	@Job(name = "Webhook Execution Job", retries = 0)
	public void executeWebhook(String webhookId, String payloadJson, String correlationId, int attempt) {
		Short id = Short.parseShort(webhookId);
		Optional<Webhook> webhookOpt = webhookRepository.findById(id);
		if (webhookOpt.isEmpty()) {
			return;
		}

		Webhook webhook = webhookOpt.get();
		WebhookLog webhookLog = createWebhookLog(webhook, payloadJson, correlationId, attempt);

		try {
			webhookLog.setExecutionStatus(WebhookExecutionStatus.IN_PROGRESS);
			webhookLog.setExecutedAt(LocalDateTime.now());

			int maxRetries = webhook.getRetry() != null ? webhook.getRetry() : 0;
			int timeoutMs = webhookProperties.readTimeoutMs() != null ? webhookProperties.readTimeoutMs() : 30_000;

			WebhookRequest request = WebhookRequest.builder()
					.webhookId(String.valueOf(webhook.getId()))
					.url(webhook.getUrl())
					.payload(payloadJson)
					.correlationId(correlationId)
					.attemptNumber(attempt + 1)
					.maxRetries(maxRetries)
					.timeoutMs(timeoutMs)
					.scheduledAt(webhookLog.getScheduledAt())
					.jobId(webhookLog.getJobId())
					.build();

			WebhookResponse response = webhookExecutor.execute(request);

			updateWebhookLogFromResponse(webhookLog, response);

			if (retryStrategy.shouldRetry(response, attempt, maxRetries)) {
				scheduleRetry(webhook, payloadJson, correlationId, attempt);
				webhookLog.setExecutionStatus(WebhookExecutionStatus.RETRIED);
			}

		} catch (Exception e) {
			webhookLog.setExecutionStatus(WebhookExecutionStatus.FAILED);
			webhookLog.setErrorMessage("Unexpected error: " + e.getMessage());
			webhookLog.setCompletedAt(LocalDateTime.now());
		} finally {
			webhookLogRepository.save(webhookLog);
		}
	}

	@Override
	public Map<String, Object> getWebhookStats(String webhookId) {
		Short id = Short.parseShort(webhookId);
		Optional<Webhook> webhookOpt = webhookRepository.findById(id);
		if (webhookOpt.isEmpty()) {
			return Map.of("error", "Webhook not found");
		}

		Object[] stats = webhookLogRepository.getSuccessRateStats(id);
		long totalAttempts = (Long) stats[0];
		long successfulAttempts = (Long) stats[1];

		double successRate = totalAttempts > 0 ? (double) successfulAttempts / totalAttempts * 100 : 0;

		return Map.of(
				"webhookId", webhookId,
				"totalAttempts", totalAttempts,
				"successfulAttempts", successfulAttempts,
				"failedAttempts", totalAttempts - successfulAttempts,
				"successRate", String.format("%.2f%%", successRate));
	}

	@Override
	public List<WebhookLog> getWebhookLogs(String webhookId, int page, int size) {
		Short id = Short.parseShort(webhookId);
		return webhookLogRepository
				.findByWebhookIdOrderByCreatedAtDesc(id, org.springframework.data.domain.PageRequest.of(page, size))
				.getContent();
	}

	private List<Webhook> findActiveWebhooks(
			UUID brandId,
			UUID environmentId,
			fynxt.brand.webhook.enums.WebhookStatusType statusType,
			String webhookId) {
		if (webhookId != null) {
			Short id = Short.parseShort(webhookId);
			Optional<Webhook> webhookOpt = webhookRepository.findById(id);
			return webhookOpt
					.filter(w -> w.getStatus() == Status.ENABLED)
					.map(List::of)
					.orElse(List.of());
		} else {
			return webhookRepository.findByBrandIdAndEnvironmentIdAndStatusTypeAndStatus(
					brandId, environmentId, statusType, Status.ENABLED);
		}
	}

	private void enqueueWebhookJob(Webhook webhook, Object payload, String correlationId, int attempt) {
		String payloadJson = toJson(payload);

		Short webhookId = webhook.getId();
		jobScheduler.enqueue(() -> executeWebhook(webhookId.toString(), payloadJson, correlationId, attempt));
	}

	private void scheduleRetry(Webhook webhook, String payloadJson, String correlationId, int currentAttempt) {
		long delaySeconds = retryStrategy.calculateRetryDelaySeconds(currentAttempt);
		int nextAttempt = currentAttempt + 1;

		Short webhookId = webhook.getId();
		jobScheduler.schedule(
				LocalDateTime.now().plusSeconds(delaySeconds),
				() -> executeWebhook(webhookId.toString(), payloadJson, correlationId, nextAttempt));
	}

	private WebhookLog createWebhookLog(Webhook webhook, String payloadJson, String correlationId, int attempt) {
		return WebhookLog.builder()
				.webhookId(webhook.getId())
				.requestPayload(payloadJson)
				.attemptNumber((short) (attempt + 1))
				.executionStatus(WebhookExecutionStatus.PENDING)
				.correlationId(correlationId)
				.scheduledAt(LocalDateTime.now())
				.build();
	}

	private void updateWebhookLogFromResponse(WebhookLog webhookLog, WebhookResponse response) {
		webhookLog.setExecutionTimeMs(response.getExecutionTimeMs());
		webhookLog.setResponseStatus(response.getResponseStatus());
		webhookLog.setResponsePayload(toValidJson(response.getResponsePayload()));
		webhookLog.setErrorMessage(response.getErrorMessage());
		webhookLog.setIsSuccess(response.isSuccess());
		webhookLog.setExecutionStatus(
				response.getExecutionStatus() != null
						? WebhookExecutionStatus.valueOf(
								response.getExecutionStatus().name())
						: WebhookExecutionStatus.FAILED);
		webhookLog.setCompletedAt(response.getCompletedAt());

		if (response.getResponseHeaders() != null) {
			webhookLog.setResponseHeaders(toJson(response.getResponseHeaders()));
		}
	}

	private String toJson(Object obj) {
		try {
			return webhookObjectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return "{}";
		}
	}

	private String toValidJson(String payload) {
		if (payload == null || payload.trim().isEmpty()) {
			return null;
		}

		try {
			webhookObjectMapper.readTree(payload);
			return payload;
		} catch (Exception e) {
			try {
				return webhookObjectMapper.writeValueAsString(Map.of("response", payload));
			} catch (JsonProcessingException ex) {
				return "{}";
			}
		}
	}
}
