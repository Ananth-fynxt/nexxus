package fynxt.brand.fee.service.mappers;

import fynxt.brand.fee.dto.FeeComponentDto;
import fynxt.brand.fee.entity.EmbeddableFeeComponentId;
import fynxt.brand.fee.entity.FeeComponent;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface FeeComponentMapper {

	@Mapping(target = "feeComponentId", expression = "java(createEmbeddableFeeId(feeId, feeVersion))")
	FeeComponent toFeeComponent(FeeComponentDto componentDto, Integer feeId, Integer feeVersion);

	@Mapping(target = "id", source = "feeComponent.feeComponentId.id")
	FeeComponentDto toFeeComponentDto(FeeComponent feeComponent);

	default EmbeddableFeeComponentId createEmbeddableFeeId(Integer feeId, Integer version) {
		// Generate a unique ID for the fee component
		String id = UUID.randomUUID().toString();
		return new EmbeddableFeeComponentId(id, feeId, version);
	}
}
