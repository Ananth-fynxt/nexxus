package fynxt.brand.request.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request_risk_rules")
@IdClass(RequestRiskRuleId.class)
public class RequestRiskRule {

	@Id
	@Column(name = "request_id")
	private UUID requestId;

	@Id
	@Column(name = "risk_rule_id")
	private Integer riskRuleId;

	@Id
	@Column(name = "risk_rule_version")
	private Integer riskRuleVersion;
}
