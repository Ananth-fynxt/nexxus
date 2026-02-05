package fynxt.brand.external.dto;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmExecutionDto {
	private UUID pspId;
	private UUID token;
	private String flowTargetId;

	@Positive private Long amount;

	private String currency;
	private UUID brandId;
	private UUID environmentId;
	private String step;
	private String flowActionId;
	private String transactionId;
	private Map<String, Object> executePayload;
}
