package fynxt.brand.fee.dto;

import fynxt.brand.fee.enums.FeeComponentType;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponentDto {
	@Schema(example = "fee_component_001", accessMode = Schema.AccessMode.READ_ONLY)
	private String id;

	@NotNull(message = "Component type is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PERCENTAGE")
	private FeeComponentType type;

	@NotNull(message = "Component amount is required") @DecimalMin(value = "0.01", message = "Component amount must be greater than 0") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2.5")
	private BigDecimal amount;

	@DecimalMin(value = "0", message = "Min value must be 0 or greater") @Schema(example = "0.00")
	private BigDecimal minValue;

	@DecimalMin(value = "0", message = "Max value must be 0 or greater") @Schema(example = "10000.00")
	private BigDecimal maxValue;
}
