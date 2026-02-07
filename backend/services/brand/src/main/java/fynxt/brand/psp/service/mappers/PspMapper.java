package fynxt.brand.psp.service.mappers;

import fynxt.brand.flow.service.FlowTargetInputSchemaService;
import fynxt.brand.psp.dto.*;
import fynxt.brand.psp.entity.MaintenanceWindow;
import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.entity.PspOperation;
import fynxt.flowtarget.dto.FlowTargetDto;
import fynxt.mapper.config.MapperCoreConfig;
import fynxt.shared.dto.IdNameDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperCoreConfig.class)
public interface PspMapper {

	@Mapping(target = "credential", source = "credential", qualifiedByName = "stringToJsonNode")
	Psp toEntity(PspDto pspDto);

	PspDto toDto(Psp psp);

	@Mapping(target = "credential", source = "credential", qualifiedByName = "stringToJsonNode")
	void updateEntityFromDto(UpdatePspDto updatePspDto, @MappingTarget Psp psp);

	@Named("stringToJsonNode")
	default JsonNode stringToJsonNode(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return new ObjectMapper().readTree(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid credential JSON", e);
		}
	}

	PspSummaryDto toPspSummaryDto(Psp psp);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "name", source = "name")
	IdNameDto toIdNameDto(Psp psp);

	List<IdNameDto> toIdNameDto(List<Psp> psps);

	default Map<UUID, IdNameDto> toIdNameDtoMap(List<Psp> psps) {
		if (psps == null) {
			return Map.of();
		}
		return psps.stream().collect(Collectors.toMap(Psp::getId, this::toIdNameDto));
	}

	@Mapping(target = "pspId", source = "pspId")
	@Mapping(target = "flowActionId", source = "dto.flowActionId")
	@Mapping(target = "startAt", source = "dto.startAt", qualifiedByName = "stringToLocalDateTime")
	@Mapping(target = "endAt", source = "dto.endAt", qualifiedByName = "stringToLocalDateTime")
	MaintenanceWindow toMaintenanceWindow(UpdatePspDto.MaintenanceWindowDto dto, UUID pspId);

	@Mapping(target = "pspId", source = "pspId")
	@Mapping(target = "flowActionId", source = "dto.flowActionId")
	@Mapping(target = "flowDefinitionId", source = "dto.flowDefinitionId")
	@Mapping(target = "status", source = "dto.status")
	@Mapping(target = "currencies", source = "dto.currencies")
	@Mapping(target = "countries", source = "dto.countries")
	PspOperation toPspOperation(UpdatePspDto.PspOperationDto dto, UUID brandId, UUID environmentId, UUID pspId);

	@Named("extractCurrencies")
	default List<String> extractCurrencies(List<UpdatePspDto.CurrencyDto> currencyDtos) {
		if (currencyDtos == null) {
			return List.of();
		}
		return currencyDtos.stream().map(UpdatePspDto.CurrencyDto::getCurrency).toList();
	}

	@Mapping(target = "id", source = "psp.id")
	@Mapping(target = "credential", constant = "***ENCRYPTED***")
	@Mapping(target = "ipAddress", source = "psp.ipAddress")
	@Mapping(target = "maintenanceWindow", source = "maintenanceWindows")
	@Mapping(target = "operations", source = "operations")
	@Mapping(target = "flowTarget", source = "flowTarget")
	PspDetailsDto toPspDetailsDto(
			Psp psp,
			List<MaintenanceWindow> maintenanceWindows,
			List<PspOperation> operations,
			PspDetailsDto.FlowTargetInfo flowTarget);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "flowActionId", source = "flowActionId")
	@Mapping(target = "startAt", source = "startAt")
	@Mapping(target = "endAt", source = "endAt")
	PspDetailsDto.MaintenanceWindowDto toMaintenanceWindowDto(MaintenanceWindow maintenanceWindow);

	@Mapping(target = "flowActionId", source = "flowActionId")
	@Mapping(target = "flowDefinitionId", source = "flowDefinitionId")
	@Mapping(target = "status", source = "status")
	@Mapping(target = "currencies", source = "currencies")
	PspDetailsDto.PspOperationDto toPspOperationDto(PspOperation operation);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "credentialSchema", source = "credentialSchema")
	@Mapping(target = "flowTypeId", source = "flowTypeId")
	@Mapping(target = "currencies", ignore = true)
	@Mapping(target = "countries", ignore = true)
	@Mapping(target = "paymentMethods", ignore = true)
	@Mapping(target = "supportedActions", source = "supportedActions")
	PspDetailsDto.FlowTargetInfo toFlowTargetInfo(
			FlowTargetDto flowTargetDto, @Context FlowTargetInputSchemaService inputSchemaService);

	@AfterMapping
	default void extractInputSchemaFields(
			FlowTargetDto flowTargetDto,
			@MappingTarget PspDetailsDto.FlowTargetInfo flowTargetInfo,
			@Context FlowTargetInputSchemaService inputSchemaService) {
		if (flowTargetDto == null || flowTargetInfo == null || inputSchemaService == null) {
			return;
		}

		Object inputSchema = flowTargetDto.getInputSchema();
		if (inputSchema == null) {
			return;
		}

		try {
			flowTargetInfo.setCurrencies(inputSchemaService.extractCurrencies(inputSchema));
			flowTargetInfo.setCountries(inputSchemaService.extractCountries(inputSchema));
			flowTargetInfo.setPaymentMethods(inputSchemaService.extractPaymentMethods(inputSchema));
		} catch (Exception e) {
		}
	}

	@Mapping(target = "flowActionId", source = "flowActionId")
	@Mapping(target = "flowDefinitionId", source = "id")
	@Mapping(target = "flowActionName", source = "flowActionName")
	PspDetailsDto.SupportedActionInfo toSupportedActionInfo(FlowTargetDto.SupportedActionInfo supportedActionInfo);

	@Named("stringArrayToList")
	default List<String> stringArrayToList(String[] stringArray) {
		if (stringArray == null) {
			return List.of();
		}
		return List.of(stringArray);
	}

	@Named("stringToLocalDateTime")
	default LocalDateTime stringToLocalDateTime(String dateTimeString) {
		if (dateTimeString == null) {
			return null;
		}
		return LocalDateTime.parse(dateTimeString);
	}

	@Named("extractFlowDefinitionId")
	default String extractFlowDefinitionId(PspOperation pspOperation) {
		if (pspOperation == null) {
			return null;
		}
		return pspOperation.getFlowDefinitionId();
	}
}
