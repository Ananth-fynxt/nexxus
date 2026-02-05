package fynxt.brand.request.service.mappers;

import fynxt.brand.request.dto.RequestOutputDto;
import fynxt.brand.request.entity.RequestPsp;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface RequestPspMapper {

	@Mapping(target = "id", source = "pspId")
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "description", ignore = true)
	@Mapping(target = "logo", ignore = true)
	@Mapping(target = "brandId", ignore = true)
	@Mapping(target = "environmentId", ignore = true)
	@Mapping(target = "flowActionId", ignore = true)
	@Mapping(target = "flowDefintionId", source = "flowDefinitionId")
	RequestOutputDto.PspInfo toPspInfo(RequestPsp requestPsp);

	@Mapping(target = "requestId", source = "requestId")
	@Mapping(target = "pspId", source = "pspInfo.id")
	@Mapping(target = "flowTargetId", source = "pspInfo.flowTarget.flowTargetId")
	@Mapping(target = "flowDefinitionId", source = "pspInfo.flowDefintionId")
	@Mapping(target = "currency", source = "pspInfo.currency")
	@Mapping(target = "originalAmount", source = "pspInfo.originalAmount")
	@Mapping(target = "appliedFeeAmount", source = "pspInfo.appliedFeeAmount")
	@Mapping(target = "totalAmount", source = "pspInfo.totalAmount")
	@Mapping(target = "netAmountToUser", source = "pspInfo.netAmountToUser")
	@Mapping(target = "inclusiveFeeAmount", source = "pspInfo.inclusiveFeeAmount")
	@Mapping(target = "exclusiveFeeAmount", source = "pspInfo.exclusiveFeeAmount")
	@Mapping(target = "isFeeApplied", source = "pspInfo.feeApplied")
	RequestPsp toRequestPsp(UUID requestId, RequestOutputDto.PspInfo pspInfo);
}
