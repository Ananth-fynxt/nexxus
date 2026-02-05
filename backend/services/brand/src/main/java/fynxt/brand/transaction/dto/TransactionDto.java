package fynxt.brand.transaction.dto;

import fynxt.brand.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction data transfer object")
public class TransactionDto {
	@Schema(
			description = "Unique identifier of the brand (extracted from authentication context, read-only)",
			example = "550e8400-e29b-41d4-a716-446655440000",
			accessMode = Schema.AccessMode.READ_ONLY)
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment (extracted from authentication context, read-only)",
			example = "550e8400-e29b-41d4-a716-446655440001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private UUID environmentId;

	@Schema(
			description = "Unique transaction identifier (auto-generated, read-only)",
			example = "txn_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String txnId;

	@Schema(description = "Version number of the transaction", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private int version;

	@Schema(
			description = "Request ID associated with this transaction",
			example = "550e8400-e29b-41d4-a716-446655440002")
	private UUID requestId;

	@Schema(description = "Unique identifier of the flow action", example = "flow_action_001")
	private String flowActionId;

	@Schema(description = "Unique identifier of the flow target", example = "flow_target_001")
	private String flowTargetId;

	@Schema(description = "Unique identifier of the flow definition", example = "flow_def_001")
	private String flowDefinitionId;

	@Schema(
			description = "Unique identifier of the Payment Service Provider (PSP)",
			example = "550e8400-e29b-41d4-a716-446655440003")
	private UUID pspId;

	@Schema(
			description = "Transaction ID from the PSP (read-only)",
			example = "psp_txn_12345",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String pspTxnId;

	@Schema(description = "External request ID", example = "ext_req_001")
	private String externalRequestId;

	@Schema(description = "Type of transaction", example = "PAYMENT")
	private String transactionType;

	@Schema(description = "Current status of the transaction", example = "PENDING")
	private TransactionStatus status;

	@Schema(description = "Currency of the transaction (ISO 4217 format)", example = "USD")
	private String txnCurrency;

	@Schema(description = "Transaction fee amount", example = "2.50")
	private BigDecimal txnFee;

	@Schema(description = "Transaction amount", example = "100.00")
	private BigDecimal txnAmount;

	@Schema(description = "Payload data for transaction execution", example = "{\"paymentMethod\": \"credit_card\"}")
	private Map<String, Object> executePayload;

	@Schema(description = "Custom data associated with the transaction", example = "{\"metadata\": \"value\"}")
	private Map<String, Object> customData;

	@Schema(
			description = "Timestamp when the transaction was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@Schema(
			description = "Timestamp when the transaction was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the transaction",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the transaction",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;

	@Schema(
			description = "Customer ID associated with the transaction",
			example = "cust_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String customerId;

	@Schema(
			description = "Customer tag for categorization",
			example = "premium",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String customerTag;

	@Schema(description = "Type of customer account", example = "INDIVIDUAL", accessMode = Schema.AccessMode.READ_ONLY)
	private String customerAccountType;
}
