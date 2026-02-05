package fynxt.brand.request.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request_fees")
@IdClass(RequestFeeId.class)
public class RequestFee {

	@Id
	@Column(name = "request_id")
	private UUID requestId;

	@Id
	@Column(name = "fee_id")
	private Integer feeId;

	@Id
	@Column(name = "fee_version")
	private Integer feeVersion;
}
