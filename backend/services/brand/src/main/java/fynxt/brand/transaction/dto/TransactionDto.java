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
public class TransactionDto {
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID brandId;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440001", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID environmentId;

	@Schema(example = "txn_001", accessMode = Schema.AccessMode.READ_ONLY)
	private String txnId;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private int version;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440002")
	private UUID requestId;

	@Schema(example = "flow_action_001")
	private String flowActionId;

	@Schema(example = "flow_target_001")
	private String flowTargetId;

	@Schema(example = "flow_def_001")
	private String flowDefinitionId;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440003")
	private UUID pspId;

	@Schema(example = "psp_txn_12345", accessMode = Schema.AccessMode.READ_ONLY)
	private String pspTxnId;

	@Schema(example = "ext_req_001")
	private String externalRequestId;

	@Schema(example = "PAYMENT")
	private String transactionType;

	@Schema(example = "PENDING")
	private TransactionStatus status;

	@Schema(example = "USD")
	private String txnCurrency;

	@Schema(example = "2.50")
	private BigDecimal txnFee;

	@Schema(example = "100.00")
	private BigDecimal txnAmount;

	@Schema(example = "{\"paymentMethod\": \"credit_card\"}")
	private Map<String, Object> executePayload;

	@Schema(example = "{\"metadata\": \"value\"}")
	private Map<String, Object> customData;

	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(example = "user_789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(example = "user_789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;

	@Schema(example = "cust_001", accessMode = Schema.AccessMode.READ_ONLY)
	private String customerId;

	@Schema(example = "premium", accessMode = Schema.AccessMode.READ_ONLY)
	private String customerTag;

	@Schema(example = "INDIVIDUAL", accessMode = Schema.AccessMode.READ_ONLY)
	private String customerAccountType;
}
