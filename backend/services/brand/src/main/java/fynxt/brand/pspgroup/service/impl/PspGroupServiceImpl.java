package fynxt.brand.pspgroup.service.impl;

import fynxt.brand.psp.service.PspService;
import fynxt.brand.pspgroup.dto.PspGroupDto;
import fynxt.brand.pspgroup.entity.EmbeddablePspGroupId;
import fynxt.brand.pspgroup.entity.PspGroup;
import fynxt.brand.pspgroup.entity.PspGroupPsp;
import fynxt.brand.pspgroup.repository.PspGroupPspRepository;
import fynxt.brand.pspgroup.repository.PspGroupRepository;
import fynxt.brand.pspgroup.service.PspGroupService;
import fynxt.brand.pspgroup.service.mappers.PspGroupMapper;
import fynxt.common.enums.ErrorCode;
import fynxt.common.enums.Status;
import fynxt.flowaction.service.FlowActionService;
import fynxt.shared.dto.IdNameDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PspGroupServiceImpl implements PspGroupService {

	private final PspGroupRepository pspGroupRepository;
	private final PspGroupPspRepository pspGroupPspRepository;
	private final PspService pspService;
	private final FlowActionService flowActionService;
	private final PspGroupMapper pspGroupMapper;

	@Override
	@Transactional
	public PspGroupDto create(@Valid PspGroupDto pspGroupDto) {
		verifyPspGroupNotExists(pspGroupDto);

		Integer nextId = pspGroupRepository.getNextId();

		PspGroup pspGroup = pspGroupMapper.toPspGroup(pspGroupDto, 1);
		pspGroup.getPspGroupId().setId(nextId);
		pspGroup.setStatus(Status.ENABLED);

		PspGroup savedPspGroup = pspGroupRepository.save(pspGroup);
		createPspAssociations(savedPspGroup, pspGroupDto);
		return buildEnrichedPspGroupDto(savedPspGroup);
	}

	@Override
	@Transactional(readOnly = true)
	public PspGroupDto readLatest(Integer id) {
		PspGroup pspGroup = pspGroupRepository
				.findLatestVersionById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode()));
		return buildEnrichedPspGroupDto(pspGroup);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PspGroupDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		List<PspGroup> pspGroups = pspGroupRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
		return buildEnrichedPspGroupDtos(pspGroups);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PspGroupDto> readByPspId(UUID pspId) {
		List<PspGroupPsp> pspGroupPsps = pspGroupPspRepository.findByPspId(pspId);
		if (CollectionUtils.isEmpty(pspGroupPsps)) {
			return Collections.emptyList();
		}

		// Get unique PspGroup entities by their ID
		Map<EmbeddablePspGroupId, PspGroup> pspGroupMap = new HashMap<>();
		for (PspGroupPsp pspGroupPsp : pspGroupPsps) {
			PspGroup pspGroup = pspGroupPsp.getPspGroup();
			if (pspGroup != null) {
				EmbeddablePspGroupId id = pspGroup.getPspGroupId();
				if (!pspGroupMap.containsKey(id)) {
					// Load pspGroupPsps collection for this PspGroup
					List<PspGroupPsp> groupPsps =
							pspGroupPspRepository.findByPspGroupIdAndPspGroupVersion(id.getId(), id.getVersion());
					pspGroup.setPspGroupPsps(groupPsps);
					pspGroupMap.put(id, pspGroup);
				}
			}
		}

		return buildEnrichedPspGroupDtos(new ArrayList<>(pspGroupMap.values()));
	}

	@Override
	@Transactional
	public PspGroupDto update(Integer id, @Valid PspGroupDto pspGroupDto) {
		PspGroup existingPspGroup = pspGroupRepository
				.findLatestVersionById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode()));

		// Validate name uniqueness for update (exclude current PSP group)
		verifyPspGroupNameUniquenessForUpdate(existingPspGroup, pspGroupDto);

		Integer newVersion = existingPspGroup.getPspGroupId().getVersion() + 1;
		PspGroup updatedPspGroup = pspGroupMapper.toPspGroup(pspGroupDto, newVersion);
		updatedPspGroup.getPspGroupId().setId(existingPspGroup.getPspGroupId().getId());
		updatedPspGroup.setStatus(Status.ENABLED);

		PspGroup savedPspGroup = pspGroupRepository.save(updatedPspGroup);
		createPspAssociations(savedPspGroup, pspGroupDto);
		return buildEnrichedPspGroupDto(savedPspGroup);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		PspGroup pspGroup = pspGroupRepository
				.findLatestVersionById(id)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode()));

		pspGroup.softDelete();
		pspGroupRepository.save(pspGroup);
	}

	private void verifyPspGroupNotExists(PspGroupDto pspGroupDto) {
		if (pspGroupRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
				pspGroupDto.getBrandId(),
				pspGroupDto.getEnvironmentId(),
				pspGroupDto.getFlowActionId(),
				pspGroupDto.getName())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.PSP_GROUP_ALREADY_EXISTS.getCode());
		}
	}

	private void verifyPspGroupNameUniquenessForUpdate(PspGroup existingPspGroup, PspGroupDto pspGroupDto) {
		// Only validate if name has changed
		if (!existingPspGroup.getName().equals(pspGroupDto.getName())) {
			if (pspGroupRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
					pspGroupDto.getBrandId(),
					pspGroupDto.getEnvironmentId(),
					pspGroupDto.getFlowActionId(),
					pspGroupDto.getName())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.PSP_GROUP_ALREADY_EXISTS.getCode());
			}
		}
	}

	private void createPspAssociations(PspGroup savedPspGroup, PspGroupDto pspGroupDto) {
		if (pspGroupDto.getPsps() != null && !pspGroupDto.getPsps().isEmpty()) {
			List<PspGroupPsp> pspGroupPsps = pspGroupMapper.createPspGroupPsps(
					pspGroupDto.getPsps(),
					savedPspGroup.getPspGroupId().getId(),
					savedPspGroup.getPspGroupId().getVersion());
			// Set the pspGroup reference on each PspGroupPsp
			pspGroupPsps.forEach(pspGroupPsp -> pspGroupPsp.setPspGroup(savedPspGroup));
			List<PspGroupPsp> savedPspGroupPsps = pspGroupPspRepository.saveAll(pspGroupPsps);
			savedPspGroup.setPspGroupPsps(savedPspGroupPsps);
		}
	}

	public List<PspGroupDto> buildEnrichedPspGroupDtos(List<PspGroup> pspGroups) {
		if (CollectionUtils.isEmpty(pspGroups)) {
			return Collections.emptyList();
		}

		Map<UUID, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(pspGroups);
		Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(pspGroups);

		return buildPspGroupDtos(pspGroups, pspIdNameDtoMap, flowActionIdNameDtoMap);
	}

	public PspGroupDto buildEnrichedPspGroupDto(PspGroup pspGroup) {
		List<PspGroup> pspGroups = List.of(pspGroup);
		return buildEnrichedPspGroupDtos(pspGroups).getFirst();
	}

	private Map<UUID, IdNameDto> getPspIdNameDtoMap(List<PspGroup> pspGroups) {
		List<UUID> pspIds = getAllPspIds(pspGroups);

		if (CollectionUtils.isEmpty(pspIds)) {
			return Collections.emptyMap();
		}

		return pspService.getPspIdNameDtoMap(pspIds);
	}

	private List<UUID> getAllPspIds(List<PspGroup> pspGroups) {
		return pspGroups.stream()
				.map(PspGroup::getPspGroupPsps)
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.map(PspGroupPsp::getPspId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<PspGroup> pspGroups) {
		List<String> allFlowActionIds = getAllFlowActionIds(pspGroups);

		if (CollectionUtils.isEmpty(allFlowActionIds)) {
			return Collections.emptyMap();
		}

		return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
	}

	private List<String> getAllFlowActionIds(List<PspGroup> pspGroups) {
		return pspGroups.stream()
				.map(PspGroup::getFlowActionId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	private List<PspGroupDto> buildPspGroupDtos(
			List<PspGroup> pspGroups, Map<UUID, IdNameDto> pspMap, Map<String, IdNameDto> flowActionMap) {
		return pspGroups.stream()
				.filter(group -> Objects.nonNull(group.getPspGroupPsps()))
				.map(group -> {
					List<IdNameDto> psps = group.getPspGroupPsps().stream()
							.map(PspGroupPsp::getPspId)
							.map(pspMap::get)
							.filter(Objects::nonNull)
							.collect(Collectors.toList());
					PspGroupDto dto = pspGroupMapper.toPspGroupDto(group, psps);
					IdNameDto flowAction = flowActionMap.get(group.getFlowActionId());
					if (flowAction != null) {
						dto.setFlowActionName(flowAction.getName());
					}

					return dto;
				})
				.collect(Collectors.toList());
	}
}
