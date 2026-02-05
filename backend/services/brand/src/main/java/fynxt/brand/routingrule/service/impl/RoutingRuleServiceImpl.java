package fynxt.brand.routingrule.service.impl;

import fynxt.brand.enums.ErrorCode;
import fynxt.brand.psp.service.PspService;
import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.dto.RoutingRulePspDto;
import fynxt.brand.routingrule.dto.UpdateRoutingRuleDto;
import fynxt.brand.routingrule.entity.RoutingRule;
import fynxt.brand.routingrule.entity.RoutingRulePsp;
import fynxt.brand.routingrule.repository.RoutingRulePspRepository;
import fynxt.brand.routingrule.repository.RoutingRuleRepository;
import fynxt.brand.routingrule.service.RoutingRuleService;
import fynxt.brand.routingrule.service.mappers.RoutingRuleMapper;
import fynxt.common.service.NameUniquenessService;
import fynxt.shared.dto.IdNameDto;

import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RoutingRuleServiceImpl implements RoutingRuleService {

	private final RoutingRuleRepository routingRuleRepository;
	private final RoutingRulePspRepository pspRepository;
	private final RoutingRuleMapper routingRuleMapper;
	private final PspService pspService;
	private final NameUniquenessService nameUniquenessService;

	@Override
	@Transactional
	public RoutingRuleDto create(RoutingRuleDto routingRuleDto) {
		nameUniquenessService.validateForCreate(
				name -> routingRuleRepository.existsByBrandIdAndEnvironmentIdAndName(
						routingRuleDto.getBrandId(), routingRuleDto.getEnvironmentId(), name),
				"Routing Rule",
				routingRuleDto.getName());

		Integer nextId = routingRuleRepository.getNextId();

		RoutingRule routingRule = routingRuleMapper.toRoutingRule(routingRuleDto, 1);
		routingRule.getRoutingRuleId().setId(nextId);
		RoutingRule savedRoutingRule = routingRuleRepository.save(routingRule);
		createPsps(
				routingRuleDto.getPsps(),
				savedRoutingRule.getRoutingRuleId().getId(),
				savedRoutingRule.getRoutingRuleId().getVersion());
		return buildEnrichedRoutingRuleDto(savedRoutingRule);
	}

	@Override
	@Transactional
	public RoutingRuleDto update(Integer id, UpdateRoutingRuleDto updateRoutingRuleDto) {
		RoutingRule existingRoutingRule = getRoutingRuleIfExists(id);

		// Validate name uniqueness for update (exclude current routing rule)
		nameUniquenessService.validateForUpdate(
				name -> routingRuleRepository.existsByBrandIdAndEnvironmentIdAndNameAndIdNot(
						existingRoutingRule.getBrandId(), existingRoutingRule.getEnvironmentId(), name, id),
				"Routing Rule",
				updateRoutingRuleDto.getName(),
				existingRoutingRule.getName());

		// Create a new RoutingRule with incremented version
		RoutingRule updatedRoutingRule = routingRuleMapper.copyRoutingRuleWithIncrementedVersion(existingRoutingRule);
		routingRuleMapper.toUpdateRoutingRule(updateRoutingRuleDto, updatedRoutingRule);
		RoutingRule savedRoutingRule = routingRuleRepository.save(updatedRoutingRule);
		createPsps(
				updateRoutingRuleDto.getPsps(),
				savedRoutingRule.getRoutingRuleId().getId(),
				savedRoutingRule.getRoutingRuleId().getVersion());
		return buildEnrichedRoutingRuleDto(savedRoutingRule);
	}

	private RoutingRule getRoutingRuleIfExists(Integer id) {
		Optional<RoutingRule> existingRoutingRuleOpt =
				routingRuleRepository.findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(id);
		if (existingRoutingRuleOpt.isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					ErrorCode.ROUTING_RULE_NOT_FOUND.getCode() + " Routing rule not found with ID: " + id);
		}
		return existingRoutingRuleOpt.get();
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		RoutingRule routingRule = routingRuleRepository
				.findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						ErrorCode.ROUTING_RULE_NOT_FOUND.getCode() + " Routing rule not found with ID: " + id));

		routingRule.softDelete();
		routingRuleRepository.save(routingRule);
	}

	@Override
	public RoutingRuleDto getById(Integer id) {
		RoutingRule routingRule = getRoutingRuleIfExists(id);
		return buildEnrichedRoutingRuleDto(routingRule);
	}

	@Override
	public List<RoutingRuleDto> readAllByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		List<RoutingRule> routingRules = routingRuleRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
		return buildEnrichedRoutingRuleDtos(routingRules);
	}

	private void createPsps(List<RoutingRulePspDto> psps, Integer routingRuleId, Integer version) {
		List<RoutingRulePsp> pspList = new ArrayList<>();
		for (RoutingRulePspDto pspDto : psps) {
			RoutingRulePsp psp = routingRuleMapper.toRoutingRulePsp(pspDto, routingRuleId, version);
			pspList.add(psp);
		}
		pspRepository.saveAll(pspList);
	}

	@Override
	public RoutingRuleDto findActiveRoutingRuleById(Integer routingRuleId) {
		RoutingRule routingRule = routingRuleRepository.findActiveRoutingRuleById(routingRuleId);
		return buildEnrichedRoutingRuleDto(routingRule);
	}

	public List<RoutingRulePsp> findRoutingRulePspsByIdAndVersion(Integer routingRuleId, Integer routingRuleVersion) {
		return pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(routingRuleId, routingRuleVersion);
	}

	@Override
	public List<RoutingRuleDto> findEnabledRoutingRulesByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		List<RoutingRule> routingRules =
				routingRuleRepository.findEnabledRoutingRulesByBrandAndEnvironment(brandId, environmentId);
		return buildEnrichedRoutingRuleDtos(routingRules);
	}

	public List<RoutingRuleDto> buildEnrichedRoutingRuleDtos(List<RoutingRule> routingRules) {
		if (CollectionUtils.isEmpty(routingRules)) {
			return Collections.emptyList();
		}

		Map<UUID, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(routingRules);

		return buildRoutingRuleDtos(routingRules, pspIdNameDtoMap);
	}

	public RoutingRuleDto buildEnrichedRoutingRuleDto(RoutingRule routingRule) {
		List<RoutingRule> routingRules = List.of(routingRule);
		return buildEnrichedRoutingRuleDtos(routingRules).getFirst();
	}

	private Map<UUID, IdNameDto> getPspIdNameDtoMap(List<RoutingRule> routingRules) {
		List<UUID> pspIds = getAllPspIds(routingRules);

		if (CollectionUtils.isEmpty(pspIds)) {
			return Collections.emptyMap();
		}

		return pspService.getPspIdNameDtoMap(pspIds);
	}

	private List<UUID> getAllPspIds(List<RoutingRule> routingRules) {
		return routingRules.stream()
				.map(routingRule -> pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(
						routingRule.getRoutingRuleId().getId(),
						routingRule.getRoutingRuleId().getVersion()))
				.filter(psps -> !psps.isEmpty())
				.flatMap(List::stream)
				.map(RoutingRulePsp::getPspId)
				.distinct()
				.collect(Collectors.toList());
	}

	private List<RoutingRuleDto> buildRoutingRuleDtos(List<RoutingRule> routingRules, Map<UUID, IdNameDto> pspMap) {
		return routingRules.stream()
				.map(routingRule -> {
					RoutingRuleDto dto = routingRuleMapper.toRoutingRuleDto(routingRule);
					appendPsps(routingRule, dto, pspMap);
					return dto;
				})
				.collect(Collectors.toList());
	}

	private void appendPsps(RoutingRule routingRule, RoutingRuleDto responseDto, Map<UUID, IdNameDto> pspMap) {
		List<RoutingRulePsp> routingRulePsps = pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(
				routingRule.getRoutingRuleId().getId(),
				routingRule.getRoutingRuleId().getVersion());

		if (!routingRulePsps.isEmpty()) {
			List<RoutingRulePspDto> enrichedPsps = routingRulePsps.stream()
					.map(psp -> {
						RoutingRulePspDto dto = routingRuleMapper.toRoutingRulePspDto(psp);
						if (dto.getPspId() != null) {
							IdNameDto pspInfo = pspMap.get(dto.getPspId());
							if (pspInfo != null) {
								dto.setPspName(pspInfo.getName());
							}
						}
						return dto;
					})
					.collect(Collectors.toList());

			responseDto.setPsps(enrichedPsps);
		} else {
			responseDto.setPsps(Collections.emptyList());
		}
	}
}
