package fynxt.brand.riskrule.entity;

import fynxt.brand.riskrule.enums.RiskAction;
import fynxt.brand.riskrule.enums.RiskCustomerCriteriaType;
import fynxt.brand.riskrule.enums.RiskDuration;
import fynxt.brand.riskrule.enums.RiskType;
import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "risk_rule")
@Builder
public class RiskRule extends AuditingEntity {

	@EmbeddedId
	private EmbeddableRiskRuleId riskRuleId;

	@Column(name = "name")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", columnDefinition = "risk_type")
	private RiskType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "action", columnDefinition = "risk_action")
	private RiskAction action;

	@Column(name = "currency")
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "duration", columnDefinition = "risk_duration")
	private RiskDuration duration;

	@Column(name = "max_amount")
	private BigDecimal maxAmount;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "flow_action_id")
	private String flowActionId;

	@Enumerated(EnumType.STRING)
	@Column(name = "criteria_type", columnDefinition = "risk_customer_criteria_type")
	private RiskCustomerCriteriaType criteriaType;

	@Column(name = "criteria_value")
	private List<String> criteriaValue;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	private Status status;

	@OneToMany(mappedBy = "riskRule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<RiskRulePsp> riskRulePsps = new java.util.ArrayList<>();
}
