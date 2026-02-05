package fynxt.brand.riskrule.service;

import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.riskrule.enums.RiskAction;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.UUID;

public interface RiskRuleService {

	RiskRuleDto create(RiskRuleDto dto);

	List<RiskRuleDto> readAll();

	RiskRuleDto read(Integer id, Integer version);

	RiskRuleDto readLatest(Integer id);

	List<RiskRuleDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<RiskRuleDto> readByPspId(UUID pspId);

	List<RiskRuleDto> readByPspIds(List<UUID> pspIds);

	List<RiskRuleDto> readLatestEnabledRiskRulesByCriteria(
			List<UUID> pspIds,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			RiskAction action,
			Status status);

	RiskRuleDto update(Integer id, RiskRuleDto dto);

	void delete(Integer id);
}
