package fynxt.brand.request.service.mappers;

import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.request.entity.RequestFee;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface RequestFeeMapper {

	@Mapping(target = "requestId", source = "requestId")
	@Mapping(target = "feeId", source = "feeDto.id")
	@Mapping(target = "feeVersion", source = "feeDto.version")
	RequestFee toRequestFee(UUID requestId, FeeDto feeDto);
}
