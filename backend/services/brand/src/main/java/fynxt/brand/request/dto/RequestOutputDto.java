package fynxt.brand.request.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOutputDto {

	private UUID requestId;

	private List<PspInfo> psps;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PspInfo {
		private UUID id;
		private String name;
		private String description;
		private String logo;
		private UUID brandId;
		private UUID environmentId;
		private String flowActionId;
		private String flowDefintionId;
		private String currency;
		private BigDecimal originalAmount;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private BigDecimal appliedFeeAmount;

		private BigDecimal totalAmount;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private BigDecimal netAmountToUser;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private BigDecimal inclusiveFeeAmount;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private BigDecimal exclusiveFeeAmount;

		private boolean isFeeApplied;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private FlowTargetData flowTarget;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FlowTargetData {
		private String flowTargetId;
		private String inputSchema;
	}
}
