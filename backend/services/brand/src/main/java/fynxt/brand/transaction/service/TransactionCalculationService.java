package fynxt.brand.transaction.service;

import fynxt.brand.riskrule.enums.RiskDuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionCalculationService {

	BigDecimal calculateTotalAmount(
			UUID pspId,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime);

	BigDecimal calculateTotalAmount(
			UUID pspId,
			UUID brandId,
			UUID environmentId,
			String customerId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime);

	BigDecimal calculateCustomerTotalAmount(
			String customerId,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime);

	BigDecimal calculateCustomerTotalAmount(
			String customerTag,
			String customerAccountType,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime);

	LocalDateTime getStartTimeForDuration(RiskDuration duration, LocalDateTime currentTime);

	LocalDateTime getEndTimeForDuration(LocalDateTime currentTime);
}
