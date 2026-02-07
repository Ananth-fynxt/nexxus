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
public class WebhookDto {
	@Schema(example = "webhook-123", accessMode = Schema.AccessMode.READ_ONLY)
	private Short id;

	@NotNull(message = "Webhook status type is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "SUCCESS")
	private WebhookStatusType statusType;

	@NotBlank(message = "Webhook URL is required") @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/webhooks/transaction-status")
	private String url;

	@Min(value = 0, message = "Retry count must be at least 0") @Max(value = 10, message = "Retry count must be at most 10") @Builder.Default
	@Schema(example = "3", defaultValue = "3")
	private Integer retry = 3;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@Builder.Default
	@Schema(example = "ENABLED", defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(example = "user-789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(example = "user-789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
