package fynxt.brand.environment.service.mappers;

import fynxt.brand.environment.dto.EnvironmentDto;
import fynxt.brand.environment.entity.Environment;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class, builder = @Builder(disableBuilder = true))
public interface EnvironmentMapper {

	@Mapping(target = "id", source = "id")
	@Mapping(target = "name", source = "name")
	@Mapping(target = "brandId", source = "brandId")
	@Mapping(target = "origin", source = "origin")
	@Mapping(target = "successRedirectUrl", source = "successRedirectUrl")
	@Mapping(target = "failureRedirectUrl", source = "failureRedirectUrl")
	@Mapping(target = "createdAt", source = "createdAt")
	@Mapping(target = "updatedAt", source = "updatedAt")
	@Mapping(target = "createdBy", source = "createdBy")
	@Mapping(target = "updatedBy", source = "updatedBy")
	EnvironmentDto toEnvironmentDto(Environment environment);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	Environment toEnvironment(EnvironmentDto environmentDto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	void toUpdateEnvironment(EnvironmentDto environmentDto, @MappingTarget Environment environment);
}
