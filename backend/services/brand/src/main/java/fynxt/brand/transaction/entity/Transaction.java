package fynxt.brand.transaction.entity;

import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
public class Transaction {

	@EmbeddedId
	private EmbeddableTransactionId id;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "request_id")
	private UUID requestId;

	@Column(name = "flow_action_id")
	private String flowActionId;

	@Column(name = "flow_target_id")
	private String flowTargetId;

	@Column(name = "psp_id")
	private UUID pspId;

	@Column(name = "psp_txn_id")
	private String pspTxnId;

	@Column(name = "external_request_id")
	private String externalRequestId;

	@Column(name = "transaction_type")
	private String transactionType;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.brand.transaction.enums.TransactionStatus"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "transaction_status")
	private TransactionStatus status;

	@Column(name = "txn_currency")
	private String txnCurrency;

	@Column(name = "txn_fee")
	private BigDecimal txnFee;

	@Column(name = "txn_amount")
	private BigDecimal txnAmount;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "execute_payload", columnDefinition = "jsonb")
	private JsonNode executePayload;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "customer_tag")
	private String customerTag;

	@Column(name = "customer_account_type")
	private String customerAccountType;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	private Integer createdBy;

	@LastModifiedBy
	@Column(name = "updated_by")
	private Integer updatedBy;
}
