package fynxt.brand.transactionlimit.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitPspId implements Serializable {

	private Integer transactionLimitId;
	private Integer transactionLimitVersion;
	private UUID pspId;
}
