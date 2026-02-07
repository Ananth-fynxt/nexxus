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
public class RiskRuleDto {
	@Schema(example = "risk_rule_001", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotNull(message = "Type is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "TRANSACTION")
	private RiskType type;

	@NotNull(message = "Action is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BLOCK")
	private RiskAction action;

	@NotBlank(message = "Currency is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "USD")
	private String currency;

	@NotNull(message = "Duration is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "DAILY")
	private RiskDuration duration;

	@NotBlank(message = "Name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "High Value Transaction Risk Rule")
	private String name;

	@Schema(example = "CUSTOMER_TAG")
	private RiskCustomerCriteriaType criteriaType;

	@Schema(example = "[\"premium\", \"vip\"]")
	private List<String> criteriaValue;

	@NotNull(message = "Max amount is required") @DecimalMin(value = "0.0", inclusive = false, message = "Max amount must be greater than 0") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
	private BigDecimal maxAmount;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "Flow Action ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "flow_action_001")
	private String flowActionId;

	@Schema(example = "Process Payment", accessMode = Schema.AccessMode.READ_ONLY)
	private String flowActionName;

	@Builder.Default
	@Schema(example = "ENABLED", defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(example = "123", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(example = "123", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;

	private List<IdNameDto> psps;
}
