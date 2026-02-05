package fynxt.brand.routingrule.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routing_rule_psps")
@IdClass(RoutingRulePspId.class)
public class RoutingRulePsp {

	@Id
	@Column(name = "routing_rule_id")
	private Integer routingRuleId;

	@Id
	@Column(name = "routing_rule_version")
	private Integer routingRuleVersion;

	@Id
	@Column(name = "psp_id")
	private UUID pspId;

	@Column(name = "psp_order")
	private Integer pspOrder;

	@Column(name = "psp_value")
	private Integer pspValue;
}
