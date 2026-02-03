package fynxt.brand.branduser.service.mappers;

import fynxt.brand.branduser.dto.BrandUserDto;
import fynxt.brand.branduser.entity.BrandUser;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface BrandUserMapper {
	BrandUserDto toBrandUserDto(BrandUser brandUser);

	BrandUser toBrandUser(BrandUserDto brandUserDto);

	void toUpdateBrandUser(BrandUserDto brandUserDto, @MappingTarget BrandUser brandUser);
}
