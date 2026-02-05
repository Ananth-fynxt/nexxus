package fynxt.brand.pspgroup.service.mappers;

import fynxt.brand.pspgroup.dto.PspGroupDto;
import fynxt.brand.pspgroup.entity.EmbeddablePspGroupId;
import fynxt.brand.pspgroup.entity.PspGroup;
import fynxt.brand.pspgroup.entity.PspGroupPsp;
import fynxt.mapper.config.MapperCoreConfig;
import fynxt.shared.dto.IdNameDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperCoreConfig.class)
public interface PspGroupMapper {

	@Mapping(target = "id", source = "pspGroup.pspGroupId.id")
	@Mapping(target = "version", source = "pspGroup.pspGroupId.version")
	@Mapping(target = "psps", source = "psps")
	PspGroupDto toPspGroupDto(PspGroup pspGroup, List<IdNameDto> psps);

	@Mapping(target = "pspGroupId", expression = "java(createEmbeddablePspGroupId(pspGroupDto.getId(), version))")
	@Mapping(target = "pspGroupPsps", ignore = true)
	PspGroup toPspGroup(PspGroupDto pspGroupDto, Integer version);

	@Mapping(
			target = "pspGroupId",
			expression = "java(createEmbeddablePspGroupId(existingPspGroup.getPspGroupId().getId(), version))")
	@Mapping(
			target = "pspGroupPsps",
			expression =
					"java(mapExistingPspsToPspGroupPsps(existingPspGroup.getPspGroupPsps(), existingPspGroup.getPspGroupId().getId(), version))")
	PspGroup createUpdatedPspGroup(PspGroup existingPspGroup, Integer version);

	default EmbeddablePspGroupId createEmbeddablePspGroupId(Integer id, Integer version) {
		return new EmbeddablePspGroupId(id, version);
	}

	default List<PspGroupPsp> createPspGroupPsps(List<IdNameDto> psps, Integer pspGroupId, Integer version) {
		if (psps == null || psps.isEmpty()) {
			return List.of();
		}

		return psps.stream()
				.filter(psp -> psp != null && psp.getId() != null)
				.map(psp -> PspGroupPsp.builder()
						.pspGroupId(pspGroupId)
						.pspGroupVersion(version)
						.pspId(UUID.fromString(psp.getId()))
						.build())
				.collect(Collectors.toList());
	}

	default List<PspGroupPsp> mapExistingPspsToPspGroupPsps(
			List<PspGroupPsp> existingPsps, Integer pspGroupId, Integer version) {
		if (existingPsps == null || existingPsps.isEmpty()) {
			return List.of();
		}

		return existingPsps.stream()
				.filter(existingPsp -> existingPsp != null && existingPsp.getPspId() != null)
				.map(existingPsp -> PspGroupPsp.builder()
						.pspGroupId(pspGroupId)
						.pspGroupVersion(version)
						.pspId(existingPsp.getPspId())
						.build())
				.collect(Collectors.toList());
	}
}
