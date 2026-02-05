package fynxt.brand.routingrule.dto;

import fynxt.brand.routingrule.dto.validation.PspSelectionModeValidation;
import fynxt.brand.routingrule.enums.PspSelectionMode;
import fynxt.brand.routingrule.enums.RoutingDuration;
import fynxt.brand.routingrule.enums.RoutingType;
import fynxt.common.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
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
@PspSelectionModeValidation
@Schema(description = "Routing rule data transfer object")
public class RoutingRuleDto {

	@Schema(
			description = "Unique identifier of the routing rule (auto-generated, read-only)",
			example = "routing_rule_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(
			description = "Version number of the routing rule configuration",
			example = "1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotBlank(message = "Name is required") @Schema(
			description = "Name of the routing rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "Primary Routing Rule")
	private String name;

	@Schema(
			description = "Unique identifier of the brand associated with this routing rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this routing rule",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotNull(message = "PSP selection mode is required") @Schema(
			description = "Mode for selecting PSPs (e.g., RANDOM, ROUND_ROBIN, PRIORITY)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "ROUND_ROBIN")
	private PspSelectionMode pspSelectionMode;

	@Schema(description = "Type of routing rule (e.g., CONDITIONAL, DEFAULT)", example = "CONDITIONAL")
	private RoutingType routingType;

	@Schema(
			description = "Duration period for routing rule evaluation (e.g., HOURLY, DAILY, WEEKLY)",
			example = "DAILY")
	private RoutingDuration duration;

	@Schema(
			description = "JSON condition for routing rule evaluation (optional)",
			example = "{\"amount\": {\"$gt\": 1000}}")
	private JsonNode conditionJson;

	@Builder.Default
	@Schema(
			description = "Current status of the routing rule (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one PSP is required") @Schema(
			description = "List of PSPs with their priorities/weights for this routing rule",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<RoutingRulePspDto> psps;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the routing rule was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the routing rule was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the routing rule",
			example = "123",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the routing rule",
			example = "123",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
