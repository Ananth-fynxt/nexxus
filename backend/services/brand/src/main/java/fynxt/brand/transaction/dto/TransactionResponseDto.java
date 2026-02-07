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
public class TransactionResponseDto {

	@Schema(example = "ortxlwzD0R7tUurZ")
	private String txnId;

	@Schema(example = "true")
	private Boolean txnSuccess;

	@Schema(example = "{\"http\": \"{}\", \"logs\": {}}")
	private Object txnMeta;

	@Schema(example = "ERR_INVALID_PAYMENT_METHOD")
	private String txnError;

	@Schema(example = "https://pay.sticpay.com/...")
	private String sessionUrl;
}
