package fynxt.brand.riskrule.dto;

import fynxt.brand.riskrule.dto.validation.ValidCustomerCriteria;
import fynxt.brand.riskrule.enums.RiskAction;
import fynxt.brand.riskrule.enums.RiskCustomerCriteriaType;
import fynxt.brand.riskrule.enums.RiskDuration;
import fynxt.brand.riskrule.enums.RiskType;
import fynxt.common.enums.Status;
import fynxt.shared.dto.IdNameDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidCustomerCriteria
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(description = "Risk rule data transfer object")
public class RiskRuleDto {
	@Schema(
			description = "Unique identifier of the risk rule (auto-generated, read-only)",
			example = "risk_rule_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(
			description = "Version number of the risk rule configuration",
			example = "1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotNull(message = "Type is required") @Schema(
			description = "Type of risk rule (e.g., TRANSACTION, CUSTOMER)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "TRANSACTION")
	private RiskType type;

	@NotNull(message = "Action is required") @Schema(
			description = "Action to take when risk rule is triggered (e.g., BLOCK, ALLOW, REVIEW)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "BLOCK")
	private RiskAction action;

	@NotBlank(message = "Currency is required") @Schema(
			description = "Currency code (ISO 4217 format)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "USD")
	private String currency;

	@NotNull(message = "Duration is required") @Schema(
			description = "Duration period for risk rule evaluation (e.g., HOURLY, DAILY, WEEKLY)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "DAILY")
	private RiskDuration duration;

	@NotBlank(message = "Name is required") @Schema(
			description = "Name of the risk rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "High Value Transaction Risk Rule")
	private String name;

	@Schema(description = "Type of customer criteria for risk rule evaluation", example = "CUSTOMER_TAG")
	private RiskCustomerCriteriaType criteriaType;

	@Schema(
			description = "List of criteria values (e.g., customer tags, customer IDs)",
			example = "[\"premium\", \"vip\"]")
	private List<String> criteriaValue;

	@NotNull(message = "Max amount is required") @DecimalMin(value = "0.0", inclusive = false, message = "Max amount must be greater than 0") @Schema(
			description = "Maximum transaction amount threshold for this risk rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "10000.00")
	private BigDecimal maxAmount;

	@Schema(
			description = "Unique identifier of the brand associated with this risk rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this risk rule",
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
			description = "Current status of the risk rule (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the risk rule was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the risk rule was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the risk rule",
			example = "123",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the risk rule",
			example = "123",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;

	@Schema(description = "List of Payment Service Providers (PSPs) associated with this risk rule")
	private List<IdNameDto> psps;
}
