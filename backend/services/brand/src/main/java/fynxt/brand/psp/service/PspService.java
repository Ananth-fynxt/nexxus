package fynxt.brand.psp.service;

import fynxt.brand.psp.dto.*;
import fynxt.brand.psp.entity.Psp;
import fynxt.shared.dto.IdNameDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PspService {

	PspDto create(PspDto pspDto);

	PspDetailsDto update(UUID pspId, UpdatePspDto pspDto);

	PspDetailsDto getById(UUID pspId);

	List<PspSummaryDto> getByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
			UUID brandId, UUID environmentId, String status, String currency, String flowActionId);

	List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndFlowAction(
			UUID brandId, UUID environmentId, String status, String flowActionId);

	List<String> getSupportedCurrenciesByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<String> getSupportedCountriesByBrandAndEnvironment(UUID brandId, UUID environmentId);

	Psp getPspIfEnabled(UUID pspId);

	PspSummaryDto updateStatus(UUID pspId, String status);

	Map<UUID, IdNameDto> getPspIdNameDtoMap(List<UUID> pspIds);
}
