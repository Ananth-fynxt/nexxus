package fynxt.brand.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
		description =
				"Response payload for transaction creation containing only the transaction id and VM execution response")
public class TransactionSession {

	@Schema(description = "Unique transaction identifier", example = "ortxlwzD0R7tUurZ")
	private String txnId;

	@Schema(description = "Transaction data", example = "{\"data\": \"{}\"}")
	private Object txnData;
}
