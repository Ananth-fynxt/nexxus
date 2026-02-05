package fynxt.brand.transaction.dto;

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
public class TransactionResponseDto {

	@Schema(description = "Unique transaction identifier", example = "ortxlwzD0R7tUurZ")
	private String txnId;

	@Schema(description = "Transaction success", example = "true")
	private Boolean txnSuccess;

	@Schema(description = "Transaction meta", example = "{\"http\": \"{}\", \"logs\": {}}")
	private Object txnMeta;

	@Schema(description = "Transaction error", example = "ERR_INVALID_PAYMENT_METHOD")
	private String txnError;

	@Schema(description = "Session URL for the transaction", example = "https://pay.sticpay.com/...")
	private String sessionUrl;
}
