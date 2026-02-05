package fynxt.brand.transactionlimit.entity;

import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_limits")
@Builder
public class TransactionLimit extends AuditingEntity {

	@EmbeddedId
	private EmbeddableTransactionLimitId transactionLimitId;

	@Column(name = "name")
	private String name;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "currency")
	private String currency;

	@Column(name = "countries", columnDefinition = "TEXT[]")
	private String[] countries;

	@Column(name = "customer_tags", columnDefinition = "TEXT[]")
	private String[] customerTags;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;
}
