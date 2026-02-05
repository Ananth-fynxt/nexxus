package fynxt.brand.webhook.dto;

import fynxt.brand.webhook.enums.WebhookStatusType;
import fynxt.common.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Webhook configuration data transfer object")
public class WebhookDto {
	@Schema(
			description = "Unique identifier of the webhook (auto-generated, read-only)",
			example = "webhook-123",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Short id;

	@NotNull(message = "Webhook status type is required") @Schema(
			description = "Type of status event that triggers this webhook (SUCCESS or FAILURE or NOTIFICATION)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "SUCCESS")
	private WebhookStatusType statusType;

	@NotBlank(message = "Webhook URL is required") @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://") @Schema(
			description = "HTTP/HTTPS endpoint URL where webhook notifications will be sent",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "https://example.com/webhooks/transaction-status")
	private String url;

	@Min(value = 0, message = "Retry count must be at least 0") @Max(value = 10, message = "Retry count must be at most 10") @Builder.Default
	@Schema(
			description = "Number of retry attempts if webhook delivery fails (0-10)",
			example = "3",
			defaultValue = "3")
	private Integer retry = 3;

	@Schema(
			description = "Unique identifier of the brand associated with this webhook",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this webhook",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@Builder.Default
	@Schema(
			description = "Current status of the webhook (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the webhook was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the webhook was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the webhook",
			example = "user-789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the webhook",
			example = "user-789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
