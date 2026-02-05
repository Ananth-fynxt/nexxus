package fynxt.brand.routingrule.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutingRulePspId implements Serializable {
	private Integer routingRuleId;
	private Integer routingRuleVersion;
	private UUID pspId;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		RoutingRulePspId that = (RoutingRulePspId) obj;
		return Objects.equals(routingRuleId, that.routingRuleId)
				&& Objects.equals(routingRuleVersion, that.routingRuleVersion)
				&& Objects.equals(pspId, that.pspId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(routingRuleId, routingRuleVersion, pspId);
	}

	@Override
	public String toString() {
		return "RoutingRulePspId{"
				+ "routingRuleId='"
				+ routingRuleId
				+ '\''
				+ ", routingRuleVersion="
				+ routingRuleVersion
				+ ", pspId='"
				+ pspId
				+ '\''
				+ '}';
	}
}
