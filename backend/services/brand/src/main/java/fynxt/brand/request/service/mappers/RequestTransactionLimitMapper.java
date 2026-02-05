package fynxt.brand.request.service.mappers;

import fynxt.brand.request.entity.RequestTransactionLimit;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface RequestTransactionLimitMapper {

	@Mapping(target = "requestId", source = "requestId")
	@Mapping(target = "transactionLimitId", source = "transactionLimitDto.id")
	@Mapping(target = "transactionLimitVersion", source = "transactionLimitDto.version")
	RequestTransactionLimit toRequestTransactionLimit(UUID requestId, TransactionLimitDto transactionLimitDto);
}
