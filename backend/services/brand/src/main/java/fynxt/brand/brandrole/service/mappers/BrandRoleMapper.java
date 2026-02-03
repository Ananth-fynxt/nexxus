package fynxt.brand.brandrole.service.mappers;

import fynxt.brand.brandrole.dto.BrandRoleDto;
import fynxt.brand.brandrole.entity.BrandRole;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface BrandRoleMapper {
	@Mapping(target = "permission", source = "permission")
	BrandRoleDto toBrandRoleDto(BrandRole brandRole);

	@Mapping(target = "permission", source = "permission")
	BrandRole toBrandRole(BrandRoleDto brandRoleDto);

	@Mapping(target = "permission", source = "permission")
	void toUpdateBrandRole(BrandRoleDto brandRoleDto, @MappingTarget BrandRole brandRole);
}
