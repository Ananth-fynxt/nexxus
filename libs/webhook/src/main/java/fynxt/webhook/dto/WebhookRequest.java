package fynxt.webhook.dto;

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
public class WebhookRequest {

	private String webhookId;
	private String url;
	private String payload;
	private String correlationId;
	private int attemptNumber;
	private int maxRetries;
	private Map<String, String> headers;
	private int timeoutMs;
	private LocalDateTime scheduledAt;
	private String jobId;
}
