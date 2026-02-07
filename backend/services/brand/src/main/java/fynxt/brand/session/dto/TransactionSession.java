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
public class TransactionSession {

	@Schema(example = "ortxlwzD0R7tUurZ")
	private String txnId;

	@Schema(example = "{\"data\": \"{}\"}")
	private Object txnData;
}
