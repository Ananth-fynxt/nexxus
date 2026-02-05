package fynxt.brand.request.service.mappers;

import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.request.entity.Request;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface RequestMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "flowActionId", source = "actionId")
	Request toRequest(RequestInputDto requestInputDto);
}
