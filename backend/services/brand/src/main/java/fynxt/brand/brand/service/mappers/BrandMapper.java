package fynxt.brand.brand.service.mappers;

import fynxt.brand.brand.dto.BrandDto;
import fynxt.brand.brand.entity.Brand;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface BrandMapper {
	BrandDto toBrandDto(Brand brand);

	Brand toBrand(BrandDto brandDto);

	void toUpdateBrand(BrandDto brandDto, @MappingTarget Brand brand);
}
