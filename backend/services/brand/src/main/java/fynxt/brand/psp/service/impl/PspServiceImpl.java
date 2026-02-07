package fynxt.brand.psp.service.impl;

import fynxt.brand.flow.service.FlowTargetInputSchemaService;
import fynxt.brand.psp.dto.*;
import fynxt.brand.psp.entity.MaintenanceWindow;
import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.entity.PspOperation;
import fynxt.brand.psp.repository.MaintenanceWindowRepository;
import fynxt.brand.psp.repository.PspOperationRepository;
import fynxt.brand.psp.repository.PspRepository;
import fynxt.brand.psp.service.PspService;
import fynxt.brand.psp.service.mappers.PspMapper;
import fynxt.common.enums.ErrorCode;
import fynxt.common.enums.Status;
import fynxt.common.service.NameUniquenessService;
import fynxt.common.util.CryptoUtil;
import fynxt.flowtarget.dto.FlowTargetDto;
import fynxt.flowtarget.service.FlowTargetService;
import fynxt.shared.dto.IdNameDto;

import java.util.*;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PspServiceImpl implements PspService {

	private final PspRepository pspRepository;
	private final MaintenanceWindowRepository maintenanceWindowRepository;
	private final PspOperationRepository pspOperationRepository;
	private final FlowTargetService flowTargetService;
	private final FlowTargetInputSchemaService inputSchemaService;
	private final PspMapper pspMapper;
	private final NameUniquenessService nameUniquenessService;
	private final CryptoUtil cryptoUtil;

	@Override
	@Transactional
	public PspDto create(PspDto pspDto) {
		flowTargetService.validateCredentialsForFlowTarget(pspDto.getFlowTargetId(), pspDto.getCredential());
		verifyPspDoesNotExists(pspDto);
		Psp psp = pspMapper.toEntity(pspDto);
		encryptCredentials(psp);
		Psp savedPsp = pspRepository.save(psp);
		return pspMapper.toDto(savedPsp);
	}

	private void verifyPspDoesNotExists(PspDto pspDto) {
		nameUniquenessService.validateForCreate(
				name -> pspRepository.existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
						pspDto.getBrandId(), pspDto.getEnvironmentId(), pspDto.getFlowTargetId(), name),
				"PSP",
				pspDto.getName());
	}

	@Override
	public PspDetailsDto getById(UUID pspId) {
		Psp psp = getPspIfExists(pspId);
		List<MaintenanceWindow> maintenanceWindows = maintenanceWindowRepository.findByPspId(pspId);
		List<PspOperation> operations = pspOperationRepository.findByPspId(pspId);
		FlowTargetDto flowTargetDto = flowTargetService.readWithAssociations(psp.getFlowTargetId());
		PspDetailsDto.FlowTargetInfo flowTargetInfo = pspMapper.toFlowTargetInfo(flowTargetDto, inputSchemaService);
		return pspMapper.toPspDetailsDto(psp, maintenanceWindows, operations, flowTargetInfo);
	}

	private Psp getPspIfExists(UUID pspId) {
		return pspRepository
				.findById(pspId)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_NOT_FOUND.getCode()));
	}

	@Override
	public List<PspSummaryDto> getByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		return pspRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
				.map(pspMapper::toPspSummaryDto)
				.toList();
	}

	@Override
	public List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
			UUID brandId, UUID environmentId, String status, String currency, String flowActionId) {
		return pspRepository
				.findByBrandEnvStatusCurrencyAndFlowAction(brandId, environmentId, status, currency, flowActionId)
				.stream()
				.map(pspMapper::toPspSummaryDto)
				.toList();
	}

	@Override
	public List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndFlowAction(
			UUID brandId, UUID environmentId, String status, String flowActionId) {
		return pspRepository.findByBrandEnvStatusAndFlowAction(brandId, environmentId, status, flowActionId).stream()
				.map(pspMapper::toPspSummaryDto)
				.toList();
	}

	@Override
	public List<String> getSupportedCurrenciesByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		return pspRepository.findSupportedCurrenciesByBrandAndEnvironment(brandId, environmentId);
	}

	@Override
	public List<String> getSupportedCountriesByBrandAndEnvironment(UUID brandId, UUID environmentId) {
		return pspRepository.findSupportedCountriesByBrandAndEnvironment(brandId, environmentId);
	}

	@Override
	@Transactional
	public PspDetailsDto update(UUID pspId, UpdatePspDto pspDto) {
		Psp existingPsp = getPspIfExists(pspId);
		validateCredentialsCountriesAndCurrencies(pspDto, existingPsp);

		nameUniquenessService.validateForUpdate(
				name -> pspRepository.existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
						existingPsp.getBrandId(), existingPsp.getEnvironmentId(), existingPsp.getFlowTargetId(), name),
				"PSP",
				pspDto.getName(),
				existingPsp.getName());

		pspMapper.updateEntityFromDto(pspDto, existingPsp);
		if (pspDto.getCredential() != null) {
			encryptCredentials(existingPsp);
		}

		deleteAllExistingConfiguration(pspId);

		List<MaintenanceWindow> latestMaintenanceWindow = createLatestMaintenanceWindow(pspId, pspDto);
		List<PspOperation> latestOperations = createLatestOperations(pspId, pspDto);
		Psp saved = pspRepository.save(existingPsp);
		FlowTargetDto flowTargetDto = flowTargetService.readWithAssociations(existingPsp.getFlowTargetId());
		PspDetailsDto.FlowTargetInfo flowTargetInfo = pspMapper.toFlowTargetInfo(flowTargetDto, inputSchemaService);

		return pspMapper.toPspDetailsDto(saved, latestMaintenanceWindow, latestOperations, flowTargetInfo);
	}

	private List<MaintenanceWindow> createLatestMaintenanceWindow(UUID pspId, UpdatePspDto pspDto) {
		List<UpdatePspDto.MaintenanceWindowDto> maintenanceWindow = pspDto.getMaintenanceWindow();
		if (CollectionUtils.isEmpty(maintenanceWindow)) {
			return Collections.emptyList();
		}
		List<MaintenanceWindow> maintenanceWindows = maintenanceWindow.stream()
				.map(dto -> pspMapper.toMaintenanceWindow(dto, pspId))
				.toList();
		return maintenanceWindowRepository.saveAll(maintenanceWindows);
	}

	private List<PspOperation> createLatestOperations(UUID pspId, UpdatePspDto pspDto) {
		List<UpdatePspDto.PspOperationDto> operations = pspDto.getOperations();
		if (CollectionUtils.isEmpty(operations)) {
			return Collections.emptyList();
		}

		List<PspOperation> pspOperations = operations.stream()
				.map(dto -> pspMapper.toPspOperation(dto, pspDto.getBrandId(), pspDto.getEnvironmentId(), pspId))
				.toList();
		return pspOperationRepository.saveAll(pspOperations);
	}

	private void deleteAllExistingConfiguration(UUID pspId) {
		// Soft delete all maintenance windows for this PSP
		List<MaintenanceWindow> maintenanceWindows = maintenanceWindowRepository.findByPspId(pspId);
		for (MaintenanceWindow maintenanceWindow : maintenanceWindows) {
			maintenanceWindow.softDelete();
			maintenanceWindowRepository.save(maintenanceWindow);
		}
		// PspOperation doesn't extend AuditingEntity, so hard delete is required
		pspOperationRepository.deleteByPspId(pspId);
	}

	private void validateCredentialsCountriesAndCurrencies(UpdatePspDto pspDto, Psp existingPsp) {
		String flowTargetId =
				pspDto.getFlowTargetId() != null ? pspDto.getFlowTargetId() : existingPsp.getFlowTargetId();

		if (pspDto.getCredential() != null) {
			flowTargetService.validateCredentialsForFlowTarget(flowTargetId, pspDto.getCredential());
		}

		if (pspDto.getOperations() != null && !pspDto.getOperations().isEmpty()) {
			FlowTargetDto flowTargetDto = flowTargetService.readWithAssociations(flowTargetId);
			Object inputSchema = flowTargetDto.getInputSchema();

			if (inputSchema != null) {
				List<String> operationCurrencies = extractCurrenciesFromOperations(pspDto.getOperations());
				List<String> operationCountries = extractCountriesFromOperations(pspDto.getOperations());

				if (!operationCurrencies.isEmpty()) {
					inputSchemaService.validateCurrencies(operationCurrencies, inputSchema);
				}
				if (!operationCountries.isEmpty()) {
					inputSchemaService.validateCountries(operationCountries, inputSchema);
				}
			}
		}
	}

	private List<String> extractCurrenciesFromOperations(List<UpdatePspDto.PspOperationDto> operations) {
		if (operations == null) {
			return List.of();
		}

		Set<String> currencies = new HashSet<>();
		for (UpdatePspDto.PspOperationDto operation : operations) {
			currencies.addAll(operation.getCurrencies());
		}
		return new ArrayList<>(currencies);
	}

	private List<String> extractCountriesFromOperations(List<UpdatePspDto.PspOperationDto> operations) {
		if (operations == null) {
			return List.of();
		}

		return operations.stream()
				.filter(operation -> operation.getCountries() != null)
				.flatMap(operation -> operation.getCountries().stream())
				.distinct()
				.toList();
	}

	@Override
	public Psp getPspIfEnabled(UUID pspId) {
		Psp psp = getPspIfExists(pspId);
		if (psp.getStatus() != Status.ENABLED) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_STATUS_INVALID.getCode());
		}
		return psp;
	}

	@Override
	@Transactional
	public PspSummaryDto updateStatus(UUID pspId, String status) {
		Psp psp = getPspIfExists(pspId);
		psp.setStatus(Status.valueOf(status));
		Psp saved = pspRepository.save(psp);
		return pspMapper.toPspSummaryDto(saved);
	}

	@Override
	public Map<UUID, IdNameDto> getPspIdNameDtoMap(List<UUID> pspIds) {
		if (CollectionUtils.isEmpty(pspIds)) {
			return Map.of();
		}

		List<Psp> psps = pspRepository.findAllById(pspIds);
		return pspMapper.toIdNameDtoMap(psps);
	}

	private void encryptCredentials(Psp psp) {
		if (psp.getCredential() != null) {
			try {
				psp.setCredential(cryptoUtil.encryptCredentialJsonNode(psp.getCredential()));
			} catch (Exception e) {
				throw new RuntimeException("Failed to encrypt credentials", e);
			}
		}
	}
}
