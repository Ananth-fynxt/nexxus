package fynxt.brand.transaction.service;

import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.enums.RoutingDuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RoutingCalculationService {

	Map<UUID, RoutingCalculationResult> calculateRoutingThresholds(
			List<RoutingRuleDto> routingRules,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime);

	LocalDateTime getStartTimeForDuration(RoutingDuration duration, LocalDateTime currentTime);

	LocalDateTime getEndTimeForDuration(LocalDateTime currentTime);

	record RoutingCalculationResult(BigDecimal totalAmount, Long transactionCount, BigDecimal percentage) {}
}
