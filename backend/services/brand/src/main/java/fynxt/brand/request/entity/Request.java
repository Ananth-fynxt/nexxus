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
@Table(name = "requests")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
public class Request {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "brand_id", nullable = false)
	private UUID brandId;

	@Column(name = "environment_id", nullable = false)
	private UUID environmentId;

	@Column(name = "customer_id", nullable = false)
	private String customerId;

	@Column(name = "customer_tag")
	private String customerTag;

	@Column(name = "customer_account_type")
	private String customerAccountType;

	@Column(name = "flow_action_id", nullable = false)
	private String flowActionId;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "currency")
	private String currency;

	@Column(name = "country")
	private String country;

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
