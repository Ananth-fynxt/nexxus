package fynxt.brand.fee.service;

import fynxt.brand.fee.dto.FeeDto;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.UUID;

public interface FeeService {

	FeeDto create(FeeDto feeDto);

	FeeDto readLatest(Integer id);

	List<FeeDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<FeeDto> readByPspId(UUID pspId);

	List<FeeDto> readLatestEnabledFeeRulesByCriteria(
			List<UUID> pspIds, UUID brandId, UUID environmentId, String flowActionId, String currency, Status status);

	FeeDto update(Integer id, FeeDto feeDto);

	void delete(Integer id);
}
