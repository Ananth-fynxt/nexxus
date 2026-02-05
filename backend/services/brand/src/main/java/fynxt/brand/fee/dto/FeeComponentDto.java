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
@Schema(description = "Fee component data transfer object representing a single fee component")
public class FeeComponentDto {
	@Schema(
			description = "Unique identifier of the fee component (auto-generated, read-only)",
			example = "fee_component_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String id;

	@NotNull(message = "Component type is required") @Schema(
			description = "Type of fee component (FIXED, PERCENTAGE, etc.)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "PERCENTAGE")
	private FeeComponentType type;

	@NotNull(message = "Component amount is required") @DecimalMin(value = "0.01", message = "Component amount must be greater than 0") @Schema(
			description = "Amount or percentage value for the fee component",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "2.5")
	private BigDecimal amount;

	@DecimalMin(value = "0", message = "Min value must be 0 or greater") @Schema(description = "Minimum value threshold for applying this fee component", example = "0.00")
	private BigDecimal minValue;

	@DecimalMin(value = "0", message = "Max value must be 0 or greater") @Schema(description = "Maximum value threshold for applying this fee component", example = "10000.00")
	private BigDecimal maxValue;
}
