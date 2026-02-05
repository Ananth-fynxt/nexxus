package fynxt.brand.transactionlimit.service.mappers;

import fynxt.brand.transactionlimit.dto.TransactionLimitPspActionDto;
import fynxt.brand.transactionlimit.entity.TransactionLimitPspAction;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface TransactionLimitPspActionMapper {

	@Mapping(target = "transactionLimitId", source = "transactionLimitId")
	@Mapping(target = "transactionLimitVersion", source = "transactionLimitVersion")
	TransactionLimitPspAction toTransactionLimitPspAction(
			TransactionLimitPspActionDto pspActionDto, Integer transactionLimitId, Integer transactionLimitVersion);

	TransactionLimitPspActionDto toTransactionLimitPspActionDto(TransactionLimitPspAction pspAction);
}
