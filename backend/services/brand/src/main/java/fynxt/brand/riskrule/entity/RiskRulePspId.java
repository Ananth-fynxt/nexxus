package fynxt.brand.riskrule.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskRulePspId implements Serializable {
	private Integer riskRuleId;
	private Integer riskRuleVersion;
	private UUID pspId;
}
