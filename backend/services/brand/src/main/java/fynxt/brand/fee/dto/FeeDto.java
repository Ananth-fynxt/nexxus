package fynxt.brand.fee.dto;

import fynxt.brand.fee.dto.validation.ValidFeeComponents;
import fynxt.brand.fee.enums.ChargeFeeType;
import fynxt.common.enums.Status;
import fynxt.shared.dto.IdNameDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fee configuration data transfer object")
public class FeeDto {
	@Schema(
			description = "Unique identifier of the fee (auto-generated, read-only)",
			example = "fee_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(
			description = "Version number of the fee configuration",
			example = "1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotBlank(message = "Fee name is required") @Schema(
			description = "Name of the fee configuration",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "Standard Transaction Fee")
	private String name;

	@NotBlank(message = "Currency is required") @Schema(
			description = "Currency code (ISO 4217 format)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "USD")
	private String currency;

	@NotNull(message = "Charge fee type is required") @Schema(
			description = "Type of fee charge (e.g., MERCHANT, CUSTOMER)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "MERCHANT")
	private ChargeFeeType chargeFeeType;

	@Schema(
			description = "Unique identifier of the brand associated with this fee",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this fee",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "Flow Action ID is required") @Schema(
			description = "Unique identifier of the flow action",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "flow_action_001")
	private String flowActionId;

	@Schema(
			description = "Name of the flow action (read-only, populated from flowActionId)",
			example = "Process Payment",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String flowActionName;

	@Builder.Default
	@Schema(
			description = "Current status of the fee (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one component is required") @ValidFeeComponents
	@Schema(
			description = "List of fee components that make up this fee configuration",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<FeeComponentDto> components;

	@NotEmpty(message = "At least one country is required") @Schema(
			description = "List of country codes where this fee applies (ISO 3166-1 alpha-2)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "[\"US\", \"GB\", \"CA\"]")
	private List<String> countries;

	@NotEmpty(message = "At least one PSP is required") @Schema(
			description = "List of Payment Service Providers (PSPs) associated with this fee",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<IdNameDto> psps;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the fee was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the fee was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(description = "User ID who created the fee", example = "user_789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the fee",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
