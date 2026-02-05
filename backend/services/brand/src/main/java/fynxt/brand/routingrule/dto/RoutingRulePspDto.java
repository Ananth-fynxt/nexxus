package fynxt.brand.routingrule.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingRulePspDto {

	@NotNull(message = "PSP ID is required") private UUID pspId;

	private String pspName;

	private Integer pspOrder;

	private Integer pspValue;
}
