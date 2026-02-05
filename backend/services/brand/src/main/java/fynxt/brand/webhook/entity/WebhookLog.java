package fynxt.brand.webhook.entity;

import fynxt.brand.webhook.enums.WebhookExecutionStatus;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhook_logs")
@Builder
public class WebhookLog extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "webhook_id")
	private Short webhookId;

	@Column(name = "response_status")
	private Integer responseStatus;

	@Column(name = "is_success")
	private Boolean isSuccess;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "request_payload", columnDefinition = "JSONB")
	private String requestPayload;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "response_payload", columnDefinition = "JSONB")
	private String responsePayload;

	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "execution_time_ms")
	private Integer executionTimeMs;

	@Column(name = "attempt_number")
	@Builder.Default
	private Short attemptNumber = 1;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.webhook.enums.WebhookExecutionStatus"))
	@Enumerated(EnumType.STRING)
	@Column(name = "execution_status", columnDefinition = "webhook_execution_status")
	@Builder.Default
	private WebhookExecutionStatus executionStatus = WebhookExecutionStatus.PENDING;

	@Column(name = "scheduled_at")
	private LocalDateTime scheduledAt;

	@Column(name = "executed_at")
	private LocalDateTime executedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "retry_after")
	private LocalDateTime retryAfter;

	@Column(name = "job_id")
	private String jobId;

	@Column(name = "correlation_id")
	private String correlationId;

	@Column(name = "user_agent")
	private String userAgent;

	@Column(name = "content_type")
	private String contentType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "response_headers", columnDefinition = "JSONB")
	private String responseHeaders;
}
