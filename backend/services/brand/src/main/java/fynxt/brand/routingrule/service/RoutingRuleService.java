package fynxt.brand.routingrule.service;

import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.dto.UpdateRoutingRuleDto;
import fynxt.brand.routingrule.entity.RoutingRulePsp;

import java.util.List;
import java.util.UUID;

public interface RoutingRuleService {

	RoutingRuleDto create(RoutingRuleDto routingRuleDto);

	RoutingRuleDto update(Integer id, UpdateRoutingRuleDto updateRoutingRuleDto);

	void delete(Integer id);

	RoutingRuleDto getById(Integer id);

	List<RoutingRuleDto> readAllByBrandAndEnvironment(UUID brandId, UUID environmentId);

	RoutingRuleDto findActiveRoutingRuleById(Integer routingRuleId);

	List<RoutingRulePsp> findRoutingRulePspsByIdAndVersion(Integer id, Integer version);

	List<RoutingRuleDto> findEnabledRoutingRulesByBrandAndEnvironment(UUID brandId, UUID environmentId);
}
