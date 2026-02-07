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
public class RoutingRuleDto {

	@Schema(example = "routing_rule_001", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotBlank(message = "Name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Primary Routing Rule")
	private String name;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotNull(message = "PSP selection mode is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ROUND_ROBIN")
	private PspSelectionMode pspSelectionMode;

	@Schema(example = "CONDITIONAL")
	private RoutingType routingType;

	@Schema(example = "DAILY")
	private RoutingDuration duration;

	@Schema(example = "{\"amount\": {\"$gt\": 1000}}")
	private JsonNode conditionJson;

	@Builder.Default
	@Schema(example = "ENABLED", defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one PSP is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<RoutingRulePspDto> psps;

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
}
