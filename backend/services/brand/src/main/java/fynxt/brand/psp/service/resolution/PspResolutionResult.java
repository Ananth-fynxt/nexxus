package fynxt.brand.psp.service.resolution;

import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.psp.entity.Psp;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspResolutionResult {

	private List<Psp> filteredPsps;
	private List<Psp> globalPsps;
	private List<RiskRuleDto> riskRules;
	private List<FeeDto> feeRules;
	private List<TransactionLimitDto> transactionLimits;
	private String resolvedByStrategy;
	private String routingRuleId;
	private boolean usedRoutingRuleRefinement;
	private String fetchStrategy; // "CURRENCY_ACTION" or "ACTION_ONLY"

	public boolean isEmpty() {
		return filteredPsps == null || filteredPsps.isEmpty();
	}
}
