package fynxt.brand.request.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "request_psps")
@EqualsAndHashCode
@IdClass(RequestPspId.class)
@EntityListeners(AuditingEntityListener.class)
public class RequestPsp {

	@Id
	@Column(name = "request_id")
	private UUID requestId;

	@Id
	@Column(name = "psp_id")
	private UUID pspId;

	@Column(name = "flow_target_id")
	private String flowTargetId;

	@Column(name = "flow_definition_id")
	private String flowDefinitionId;

	@Column(name = "currency")
	private String currency;

	@Column(name = "original_amount")
	private BigDecimal originalAmount;

	@Column(name = "applied_fee_amount")
	private BigDecimal appliedFeeAmount;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Column(name = "net_amount_to_user")
	private BigDecimal netAmountToUser;

	@Column(name = "inclusive_fee_amount")
	private BigDecimal inclusiveFeeAmount;

	@Column(name = "exclusive_fee_amount")
	private BigDecimal exclusiveFeeAmount;

	@Column(name = "is_fee_applied")
	private boolean isFeeApplied;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	private Integer createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", nullable = false)
	private Integer updatedBy;
}
