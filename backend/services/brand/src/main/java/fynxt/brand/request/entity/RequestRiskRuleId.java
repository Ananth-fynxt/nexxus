package fynxt.brand.request.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRiskRuleId implements Serializable {
	private UUID requestId;
	private Integer riskRuleId;
	private Integer riskRuleVersion;
}
