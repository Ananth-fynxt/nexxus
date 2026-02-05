package fynxt.brand.transactionlimit.service;

import fynxt.brand.transactionlimit.dto.TransactionLimitDto;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.UUID;

public interface TransactionLimitService {

	TransactionLimitDto create(TransactionLimitDto transactionLimitDto);

	TransactionLimitDto readLatest(Integer id);

	List<TransactionLimitDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<TransactionLimitDto> readByPspId(UUID pspId);

	List<TransactionLimitDto> readLatestEnabledTransactionLimitsByCriteria(
			List<UUID> pspIds, UUID brandId, UUID environmentId, String flowActionId, String currency, Status status);

	TransactionLimitDto update(Integer id, TransactionLimitDto transactionLimitDto);

	void delete(Integer id);
}
