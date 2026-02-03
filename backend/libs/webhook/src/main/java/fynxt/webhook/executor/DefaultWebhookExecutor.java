package fynxt.webhook.executor;

import fynxt.webhook.dto.WebhookRequest;
import fynxt.webhook.dto.WebhookResponse;
import fynxt.webhook.enums.WebhookExecutionStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class DefaultWebhookExecutor implements WebhookExecutor {

	private final RestTemplate restTemplate;

	public DefaultWebhookExecutor(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public WebhookResponse execute(WebhookRequest request) {
		long startTime = System.currentTimeMillis();
		LocalDateTime executedAt = LocalDateTime.now();

		try {
			ResponseEntity<String> response = executeHttpRequest(request);
			long executionTime = System.currentTimeMillis() - startTime;

			return buildSuccessResponse(request, response, executionTime, executedAt);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return handleHttpError(request, e, startTime, executedAt);
		} catch (ResourceAccessException e) {
			return handleTimeoutError(request, e, startTime, executedAt);
		} catch (Exception e) {
			return handleGenericError(request, e, startTime, executedAt);
		}
	}

	private ResponseEntity<String> executeHttpRequest(WebhookRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (request.getHeaders() != null) {
			request.getHeaders().forEach(headers::set);
		}

		HttpEntity<String> httpRequest = new HttpEntity<>(request.getPayload(), headers);
		return restTemplate.postForEntity(request.getUrl(), httpRequest, String.class);
	}

	private WebhookResponse buildSuccessResponse(
			WebhookRequest request, ResponseEntity<String> response, long executionTime, LocalDateTime executedAt) {

		Map<String, String> responseHeaders = new HashMap<>();
		response.getHeaders().forEach((key, values) -> {
			if (!values.isEmpty()) {
				responseHeaders.put(key, values.get(0));
			}
		});

		boolean isSuccess = response.getStatusCode().is2xxSuccessful();

		return WebhookResponse.builder()
				.webhookId(request.getWebhookId())
				.correlationId(request.getCorrelationId())
				.executionStatus(isSuccess ? WebhookExecutionStatus.SUCCESS : WebhookExecutionStatus.FAILED)
				.responseStatus(response.getStatusCode().value())
				.responsePayload(response.getBody())
				.executionTimeMs((int) executionTime)
				.attemptNumber(request.getAttemptNumber())
				.scheduledAt(request.getScheduledAt())
				.executedAt(executedAt)
				.completedAt(LocalDateTime.now())
				.jobId(request.getJobId())
				.responseHeaders(responseHeaders)
				.success(isSuccess)
				.build();
	}

	private WebhookResponse handleHttpError(
			WebhookRequest request, Exception e, long startTime, LocalDateTime executedAt) {

		long executionTime = System.currentTimeMillis() - startTime;

		Integer responseStatus = null;
		String responsePayload = null;

		if (e instanceof HttpClientErrorException httpError) {
			responseStatus = httpError.getStatusCode().value();
			responsePayload = httpError.getResponseBodyAsString();
		} else if (e instanceof HttpServerErrorException serverError) {
			responseStatus = serverError.getStatusCode().value();
			responsePayload = serverError.getResponseBodyAsString();
		}

		return WebhookResponse.builder()
				.webhookId(request.getWebhookId())
				.correlationId(request.getCorrelationId())
				.executionStatus(WebhookExecutionStatus.FAILED)
				.responseStatus(responseStatus)
				.responsePayload(responsePayload)
				.errorMessage(e.getMessage())
				.executionTimeMs((int) executionTime)
				.attemptNumber(request.getAttemptNumber())
				.scheduledAt(request.getScheduledAt())
				.executedAt(executedAt)
				.completedAt(LocalDateTime.now())
				.jobId(request.getJobId())
				.success(false)
				.build();
	}

	private WebhookResponse handleTimeoutError(
			WebhookRequest request, Exception e, long startTime, LocalDateTime executedAt) {

		long executionTime = System.currentTimeMillis() - startTime;

		return WebhookResponse.builder()
				.webhookId(request.getWebhookId())
				.correlationId(request.getCorrelationId())
				.executionStatus(WebhookExecutionStatus.FAILED)
				.errorMessage("Request timeout: " + e.getMessage())
				.executionTimeMs((int) executionTime)
				.attemptNumber(request.getAttemptNumber())
				.scheduledAt(request.getScheduledAt())
				.executedAt(executedAt)
				.completedAt(LocalDateTime.now())
				.jobId(request.getJobId())
				.success(false)
				.build();
	}

	private WebhookResponse handleGenericError(
			WebhookRequest request, Exception e, long startTime, LocalDateTime executedAt) {

		long executionTime = System.currentTimeMillis() - startTime;

		return WebhookResponse.builder()
				.webhookId(request.getWebhookId())
				.correlationId(request.getCorrelationId())
				.executionStatus(WebhookExecutionStatus.FAILED)
				.errorMessage("Unexpected error: " + e.getMessage())
				.executionTimeMs((int) executionTime)
				.attemptNumber(request.getAttemptNumber())
				.scheduledAt(request.getScheduledAt())
				.executedAt(executedAt)
				.completedAt(LocalDateTime.now())
				.jobId(request.getJobId())
				.success(false)
				.build();
	}
}
