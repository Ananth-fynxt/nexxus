package fynxt.brand.webhook.entity;

import fynxt.brand.webhook.enums.WebhookStatusType;
import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhooks")
@Builder
public class Webhook extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Short id;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.webhook.enums.WebhookStatusType"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status_type", columnDefinition = "webhook_status_type")
	private WebhookStatusType statusType;

	@Column(name = "url")
	private String url;

	@Column(name = "retry")
	@Builder.Default
	private Integer retry = 3;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;
}
