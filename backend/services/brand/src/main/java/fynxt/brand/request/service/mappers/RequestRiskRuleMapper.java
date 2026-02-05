package fynxt.brand.request.service.mappers;

import fynxt.brand.request.entity.RequestRiskRule;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface RequestRiskRuleMapper {

	@Mapping(target = "requestId", source = "requestId")
	@Mapping(target = "riskRuleId", source = "riskRuleDto.id")
	@Mapping(target = "riskRuleVersion", source = "riskRuleDto.version")
	RequestRiskRule toRequestRiskRule(UUID requestId, RiskRuleDto riskRuleDto);
}
