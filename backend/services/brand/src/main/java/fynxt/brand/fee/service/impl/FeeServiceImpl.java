package fynxt.brand.fee.service.impl;

import fynxt.brand.fee.dto.FeeComponentDto;
import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.fee.entity.Fee;
import fynxt.brand.fee.entity.FeeComponent;
import fynxt.brand.fee.entity.FeePsp;
import fynxt.brand.fee.repository.FeeComponentRepository;
import fynxt.brand.fee.repository.FeePspRepository;
import fynxt.brand.fee.repository.FeeRepository;
import fynxt.brand.fee.service.FeeService;
import fynxt.brand.fee.service.mappers.FeeComponentMapper;
import fynxt.brand.fee.service.mappers.FeeMapper;
import fynxt.brand.psp.service.PspService;
import fynxt.common.enums.ErrorCode;
import fynxt.common.enums.Status;
import fynxt.common.service.NameUniquenessService;
import fynxt.flowaction.service.FlowActionService;
import fynxt.shared.dto.IdNameDto;

import java.util.ArrayList;
import java.util.Collections;
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
public class FeeServiceImpl implements FeeService {

	private final FeeRepository feeRepository;
	private final FeeComponentRepository feeComponentRepository;
	private final FeePspRepository feePspRepository;
	private final FeeMapper feeMapper;
	private final FeeComponentMapper feeComponentMapper;
	private final PspService pspService;
	private final FlowActionService flowActionService;
	private final NameUniquenessService nameUniquenessService;

	@Override
	@Transactional
	public FeeDto create(@Valid FeeDto feeDto) {
		verifyFeeNotExists(feeDto);

		Integer nextId = feeRepository.getNextId();

		Fee fee = feeMapper.toFee(feeDto, 1);
		fee.getFeeId().setId(nextId);
		fee.setStatus(Status.ENABLED);

		Fee savedFee = feeRepository.save(fee);
		createAssociations(savedFee, feeDto);
		return buildEnrichedFeeDto(savedFee);
	}

	@Override
	public FeeDto readLatest(Integer id) {
		Fee fee = feeRepository
				.findLatestVersionById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode()));
		return buildEnrichedFeeDto(fee);
	}

	@Override
	public List<FeeDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		List<Fee> fees = feeRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
		return buildEnrichedFeeDtos(fees);
	}

	@Override
	public List<FeeDto> readByPspId(UUID pspId) {
		List<Fee> fees = feeRepository.findLatestFeesByPspId(pspId);
		return buildEnrichedFeeDtos(fees);
	}

	@Override
	public List<FeeDto> readLatestEnabledFeeRulesByCriteria(
			List<UUID> pspIds, UUID brandId, UUID environmentId, String flowActionId, String currency, Status status) {
		List<Fee> fees = feeRepository.findLatestEnabledFeeRulesByCriteria(
				pspIds, brandId, environmentId, flowActionId, currency, status);
		return buildEnrichedFeeDtos(fees);
	}

	@Override
	@Transactional
	public FeeDto update(Integer id, @Valid FeeDto feeDto) {
		Fee existingFee = feeRepository
				.findLatestVersionById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode()));

		nameUniquenessService.validateForUpdate(
				name -> feeRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
						feeDto.getBrandId(), feeDto.getEnvironmentId(), feeDto.getFlowActionId(), name),
				"Fee",
				feeDto.getName(),
				existingFee.getName());

		Integer newVersion = existingFee.getFeeId().getVersion() + 1;

		Fee updatedFee = feeMapper.toFee(feeDto, newVersion);
		updatedFee.getFeeId().setId(existingFee.getFeeId().getId());
		updatedFee.setStatus(Status.ENABLED);

		Fee savedFee = feeRepository.save(updatedFee);
		createAssociations(savedFee, feeDto);
		return buildEnrichedFeeDto(savedFee);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		if (feeRepository.findLatestVersionById(id).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode());
		}

		Fee fee = feeRepository
				.findLatestVersionById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode()));
		fee.softDelete();
		feeRepository.save(fee);
	}

	private void createAssociations(Fee fee, FeeDto requestDto) {
		createPsps(fee, requestDto.getPsps());
		createComponents(fee, requestDto.getComponents());
	}

	private void createComponents(Fee fee, List<FeeComponentDto> feeComponentDtos) {
		if (feeComponentDtos != null && !feeComponentDtos.isEmpty()) {
			List<FeeComponent> feeComponents = new ArrayList<>();
			for (FeeComponentDto componentDto : feeComponentDtos) {
				FeeComponent component = feeComponentMapper.toFeeComponent(
						componentDto, fee.getFeeId().getId(), fee.getFeeId().getVersion());
				feeComponents.add(component);
			}
			feeComponentRepository.saveAll(feeComponents);
		}
	}

	private void createPsps(Fee fee, List<IdNameDto> feePspDtos) {
		if (feePspDtos != null && !feePspDtos.isEmpty()) {
			List<FeePsp> feePsps = new ArrayList<>();
			for (IdNameDto pspDto : feePspDtos) {
				if (pspDto != null && pspDto.getId() != null) {
					UUID pspId = UUID.fromString(pspDto.getId());
					FeePsp feePsp = FeePsp.builder()
							.feeId(fee.getFeeId().getId())
							.feeVersion(fee.getFeeId().getVersion())
							.pspId(pspId)
							.build();
					feePsps.add(feePsp);
				}
			}
			feePspRepository.saveAll(feePsps);
		}
	}

	public List<FeeDto> buildEnrichedFeeDtos(List<Fee> fees) {
		if (CollectionUtils.isEmpty(fees)) {
			return Collections.emptyList();
		}

		Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(fees);
		Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(fees);

		return buildFeeDtos(fees, pspIdNameDtoMap, flowActionIdNameDtoMap);
	}

	public FeeDto buildEnrichedFeeDto(Fee fee) {
		List<Fee> fees = List.of(fee);
		return buildEnrichedFeeDtos(fees).getFirst();
	}

	private Map<String, IdNameDto> getPspIdNameDtoMap(List<Fee> fees) {
		List<UUID> pspIds = getAllPspIds(fees);

		if (CollectionUtils.isEmpty(pspIds)) {
			return Collections.emptyMap();
		}

		Map<UUID, IdNameDto> uuidMap = pspService.getPspIdNameDtoMap(pspIds);
		return uuidMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
	}

	private List<UUID> getAllPspIds(List<Fee> fees) {
		return fees.stream()
				.map(fee -> feePspRepository.findByFeeIdAndFeeVersion(
						fee.getFeeId().getId(), fee.getFeeId().getVersion()))
				.filter(psps -> !psps.isEmpty())
				.flatMap(List::stream)
				.map(FeePsp::getPspId)
				.distinct()
				.collect(Collectors.toList());
	}

	private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<Fee> fees) {
		List<String> allFlowActionIds = getAllFlowActionIds(fees);

		if (CollectionUtils.isEmpty(allFlowActionIds)) {
			return Collections.emptyMap();
		}

		return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
	}

	private List<String> getAllFlowActionIds(List<Fee> fees) {
		return fees.stream()
				.map(Fee::getFlowActionId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	private List<FeeDto> buildFeeDtos(
			List<Fee> fees, Map<String, IdNameDto> pspMap, Map<String, IdNameDto> flowActionMap) {
		return fees.stream()
				.map(fee -> {
					FeeDto dto = feeMapper.toFeeDto(fee);
					appendPsps(fee, dto, pspMap);
					appendComponents(fee, dto);
					addFlowActionName(dto, flowActionMap);
					return dto;
				})
				.collect(Collectors.toList());
	}

	private void addFlowActionName(FeeDto responseDto, Map<String, IdNameDto> flowActionMap) {
		if (responseDto.getFlowActionId() != null) {
			IdNameDto flowAction = flowActionMap.get(responseDto.getFlowActionId());
			if (flowAction != null) {
				responseDto.setFlowActionName(flowAction.getName());
			}
		}
	}

	private void appendPsps(Fee fee, FeeDto responseDto, Map<String, IdNameDto> pspMap) {
		List<FeePsp> feePsps = feePspRepository.findByFeeIdAndFeeVersion(
				fee.getFeeId().getId(), fee.getFeeId().getVersion());

		if (!feePsps.isEmpty()) {
			List<String> pspIds =
					feePsps.stream().map(FeePsp::getPspId).map(UUID::toString).collect(Collectors.toList());

			List<IdNameDto> enrichedPsps = pspIds.stream()
					.map(pspId -> pspMap.getOrDefault(
							pspId, IdNameDto.builder().id(pspId).build()))
					.collect(Collectors.toList());

			responseDto.setPsps(enrichedPsps);
		} else {
			responseDto.setPsps(Collections.emptyList());
		}
	}

	private void appendComponents(Fee fee, FeeDto responseDto) {
		List<FeeComponent> feeComponents = feeComponentRepository.findByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(
				fee.getFeeId().getId(), fee.getFeeId().getVersion());
		responseDto.setComponents(feeComponents.stream()
				.map(feeComponentMapper::toFeeComponentDto)
				.toList());
	}

	private void verifyFeeNotExists(FeeDto feeDto) {
		nameUniquenessService.validateForCreate(
				name -> feeRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
						feeDto.getBrandId(), feeDto.getEnvironmentId(), feeDto.getFlowActionId(), name),
				"Fee",
				feeDto.getName());
	}
}
