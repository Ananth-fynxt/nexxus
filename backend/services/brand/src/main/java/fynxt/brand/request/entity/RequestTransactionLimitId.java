package fynxt.brand.request.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransactionLimitId implements Serializable {
	private UUID requestId;
	private Integer transactionLimitId;
	private Integer transactionLimitVersion;
}
