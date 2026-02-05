package fynxt.brand.routingrule.dto;

import fynxt.brand.routingrule.dto.validation.PspSelectionModeValidation;
import fynxt.brand.routingrule.enums.PspSelectionMode;
import fynxt.brand.routingrule.enums.RoutingDuration;
import fynxt.brand.routingrule.enums.RoutingType;
import fynxt.common.enums.Status;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PspSelectionModeValidation
public class UpdateRoutingRuleDto {

	private String name;

	private PspSelectionMode pspSelectionMode;

	private RoutingType routingType;

	private RoutingDuration duration;

	private JsonNode conditionJson;

	private Status status;

	@NotEmpty(message = "At least one PSP is required") private List<RoutingRulePspDto> psps;

	private Integer updatedBy;
}
