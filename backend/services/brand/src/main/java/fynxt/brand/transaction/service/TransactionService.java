package fynxt.brand.transaction.service;

import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.dto.TransactionSearchCriteria;
import fynxt.brand.transaction.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

public interface TransactionService {

	TransactionDto read(String txnId);

	Page<TransactionDto> readByBrandIdAndEnvironmentId(
			UUID brandId, UUID environmentId, TransactionSearchCriteria criteria);

	List<TransactionDto> readByCustomerIdAndBrandIdAndEnvironmentId(
			String customerId, UUID brandId, UUID environmentId);

	double calculateFailureRate(UUID pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime);

	double calculateFailureRateByCustomer(
			UUID pspId, String customerId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime);

	long countByPspFlowStatus(
			UUID pspId, String flowActionId, TransactionStatus status, LocalDateTime startTime, LocalDateTime endTime);

	long countByPspFlow(UUID pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime);
}
