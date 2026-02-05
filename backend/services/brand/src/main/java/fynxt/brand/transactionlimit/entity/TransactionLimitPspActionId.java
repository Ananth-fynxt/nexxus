package fynxt.brand.transactionlimit.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitPspActionId implements Serializable {

	private Integer transactionLimitId;
	private Integer transactionLimitVersion;
	private String flowActionId;
}
