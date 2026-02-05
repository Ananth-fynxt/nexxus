package fynxt.brand.riskrule.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRulePspDto {
	@NotNull(message = "PSP ID is required") private UUID id;

	private String name;
}
