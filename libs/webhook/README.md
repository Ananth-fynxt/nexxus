# Webhook Library

A reusable library for executing HTTP webhook requests with retry support.

## Features

- HTTP webhook execution with configurable timeouts
- Exponential backoff retry strategy
- Conditional auto-configuration (enabled via properties)
- Customizable through Spring beans

## Usage

### 1. Add Dependency

```kotlin
// build.gradle.kts
implementation(project(":libs:webhook"))
```

### 2. Enable in Application Properties

```yaml
# application.yml
webhook:
  enabled: true
  connect-timeout-ms: 5000      # Connection timeout (default: 5000)
  read-timeout-ms: 30000        # Read timeout (default: 30000)
  max-retries: 3                # Max retry attempts (default: 3)
  base-delay-seconds: 10        # Base delay for retries (default: 10)
  max-retry-delay-seconds: 3600 # Max retry delay cap (default: 3600)
  retry-multiplier: 2.0         # Exponential multiplier (default: 2.0)
```

### 3. Inject and Use

```java
@Service
public class MyService {

    private final WebhookExecutor webhookExecutor;
    private final RetryStrategy retryStrategy;

    public MyService(WebhookExecutor webhookExecutor, RetryStrategy retryStrategy) {
        this.webhookExecutor = webhookExecutor;
        this.retryStrategy = retryStrategy;
    }

    public void sendWebhook(String url, String payload) {
        WebhookRequest request = WebhookRequest.builder()
            .webhookId("webhook-123")
            .url(url)
            .payload(payload)
            .attemptNumber(1)
            .maxRetries(3)
            .build();

        WebhookResponse response = webhookExecutor.execute(request);

        if (!response.isSuccess() && retryStrategy.shouldRetry(response, 1, 3)) {
            long delay = retryStrategy.calculateRetryDelaySeconds(1);
            // Schedule retry after delay...
        }
    }
}
```

## Components

| Component | Description |
|-----------|-------------|
| `WebhookExecutor` | Interface for executing webhook requests |
| `DefaultWebhookExecutor` | Default implementation using RestTemplate |
| `RetryStrategy` | Interface for retry logic |
| `ExponentialBackoffRetryStrategy` | Exponential backoff implementation |
| `WebhookRequest` | Request DTO with url, payload, headers |
| `WebhookResponse` | Response DTO with status, body, timing |
| `WebhookExecutionStatus` | Enum: PENDING, IN_PROGRESS, SUCCESS, FAILED, RETRY_SCHEDULED |

## Customization

Override any bean by defining your own:

```java
@Configuration
public class CustomWebhookConfig {

    @Bean
    public RestTemplate webhookRestTemplate() {
        // Custom RestTemplate configuration
    }

    @Bean
    public RetryStrategy retryStrategy() {
        // Custom retry strategy
    }
}
```
