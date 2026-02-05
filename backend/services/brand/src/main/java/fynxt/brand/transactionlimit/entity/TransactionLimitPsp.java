package fynxt.brand.transactionlimit.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_limit_psps")
@IdClass(TransactionLimitPspId.class)
public class TransactionLimitPsp {

	@Id
	@Column(name = "transaction_limit_id")
	private Integer transactionLimitId;

	@Id
	@Column(name = "transaction_limit_version")
	private Integer transactionLimitVersion;

	@Id
	@Column(name = "psp_id")
	private UUID pspId;
}
