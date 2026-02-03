package fynxt.brand.fi.service.mappers;

import fynxt.brand.fi.dto.FiDto;
import fynxt.brand.fi.entity.Fi;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;

@Mapper(config = MapperCoreConfig.class)
public interface FiMapper {
	FiDto toFiDto(Fi fi);

	Fi toFi(FiDto fiDto);
}
