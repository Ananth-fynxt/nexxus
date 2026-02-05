package fynxt.brand.routingrule.service.mappers;

import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.dto.RoutingRulePspDto;
import fynxt.brand.routingrule.dto.UpdateRoutingRuleDto;
import fynxt.brand.routingrule.entity.EmbeddableRoutingRuleId;
import fynxt.brand.routingrule.entity.RoutingRule;
import fynxt.brand.routingrule.entity.RoutingRulePsp;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface RoutingRuleMapper {

	@Mapping(target = "id", source = "routingRuleId.id")
	@Mapping(target = "version", source = "routingRuleId.version")
	RoutingRuleDto toRoutingRuleDto(RoutingRule routingRule);

	@Mapping(target = "routingRuleId", expression = "java(createEmbeddableRoutingRuleId(dto.getId(), version))")
	RoutingRule toRoutingRule(RoutingRuleDto dto, Integer version);

	@Mapping(target = "conditionJson", source = "dto.conditionJson")
	void toUpdateRoutingRule(UpdateRoutingRuleDto dto, @MappingTarget RoutingRule routingRule);

	RoutingRulePspDto toRoutingRulePspDto(RoutingRulePsp routingRulePsp);

	@Mapping(target = "routingRuleId", source = "routingRuleId")
	@Mapping(target = "routingRuleVersion", source = "routingRuleVersion")
	RoutingRulePsp toRoutingRulePsp(RoutingRulePspDto dto, Integer routingRuleId, int routingRuleVersion);

	@Mapping(
			target = "routingRuleId",
			expression =
					"java(createEmbeddableRoutingRuleId(existing.getRoutingRuleId().getId(), existing.getRoutingRuleId().getVersion() + 1))")
	RoutingRule copyRoutingRuleWithIncrementedVersion(RoutingRule existing);

	default EmbeddableRoutingRuleId createEmbeddableRoutingRuleId(Integer id, Integer version) {
		return new EmbeddableRoutingRuleId(id, version);
	}
}
