package fynxt.brand.fee.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee_psps")
@IdClass(FeePspId.class)
public class FeePsp {

	@Id
	@Column(name = "fee_id")
	private Integer feeId;

	@Id
	@Column(name = "fee_version")
	private Integer feeVersion;

	@Id
	@Column(name = "psp_id")
	private UUID pspId;
}
