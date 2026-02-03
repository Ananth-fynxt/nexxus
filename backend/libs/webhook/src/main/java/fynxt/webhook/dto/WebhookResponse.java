package fynxt.webhook.dto;

import fynxt.webhook.enums.WebhookExecutionStatus;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {

	private String webhookId;
	private String correlationId;
	private WebhookExecutionStatus executionStatus;
	private Integer responseStatus;
	private String responsePayload;
	private String errorMessage;
	private int executionTimeMs;
	private int attemptNumber;
	private LocalDateTime scheduledAt;
	private LocalDateTime executedAt;
	private LocalDateTime completedAt;
	private String jobId;
	private Map<String, String> responseHeaders;
	private boolean success;
}
