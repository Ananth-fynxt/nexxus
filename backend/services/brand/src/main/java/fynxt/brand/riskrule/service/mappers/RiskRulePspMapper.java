package fynxt.brand.riskrule.service.mappers;

import fynxt.brand.riskrule.dto.RiskRulePspDto;
import fynxt.brand.riskrule.entity.RiskRule;
import fynxt.brand.riskrule.entity.RiskRulePsp;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RiskRulePspMapper {

	public List<RiskRulePspDto> mapPspIdsToRiskRulePspDtos(List<RiskRulePsp> riskRulePsps) {
		if (CollectionUtils.isEmpty(riskRulePsps)) {
			return List.of();
		}

		return riskRulePsps.stream()
				.map(rrp -> RiskRulePspDto.builder().id(rrp.getPspId()).build())
				.collect(Collectors.toList());
	}

	public List<RiskRulePsp> mapPspIdsToRiskRulePsps(List<UUID> pspIds, RiskRule riskRule) {
		if (pspIds == null || pspIds.isEmpty() || riskRule == null || riskRule.getRiskRuleId() == null) {
			return List.of();
		}

		Integer riskRuleId = riskRule.getRiskRuleId().getId();
		Integer riskRuleVersion = riskRule.getRiskRuleId().getVersion();

		return pspIds.stream()
				.map(pspId -> RiskRulePsp.builder()
						.riskRuleId(riskRuleId)
						.riskRuleVersion(riskRuleVersion)
						.pspId(pspId)
						.build())
				.collect(Collectors.toList());
	}
}
