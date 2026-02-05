package fynxt.brand.transaction.service.impl;

import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.enums.RoutingDuration;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.RoutingCalculationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoutingCalculationServiceImpl implements RoutingCalculationService {

	private final TransactionRepository transactionRepository;

	@Override
	public Map<UUID, RoutingCalculationResult> calculateRoutingThresholds(
			List<RoutingRuleDto> routingRules,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime) {

		Map<UUID, RoutingCalculationResult> results = new HashMap<>();

		// Extract all PSP IDs from routing rules (UUID from DTO)
		List<UUID> pspIds = routingRules.stream()
				.flatMap(rule -> rule.getPsps().stream())
				.map(p -> p.getPspId())
				.distinct()
				.collect(Collectors.toList());

		if (pspIds.isEmpty()) {
			return results;
		}

		// Single optimized query to get all PSP transaction data (repository accepts UUID directly)
		List<Object[]> rawResults = transactionRepository.findRoutingCalculationData(
				pspIds, brandId, environmentId, flowActionId, currency, startTime, endTime);

		// Process results - repository returns UUID directly
		for (Object[] row : rawResults) {
			UUID pspId = (UUID) row[0];
			BigDecimal totalAmount = (BigDecimal) row[1];
			Long transactionCount = ((Number) row[2]).longValue();

			results.put(
					pspId,
					new RoutingCalculationResult(
							totalAmount != null ? totalAmount : BigDecimal.ZERO,
							transactionCount != null ? transactionCount : 0L,
							BigDecimal.ZERO // Will be calculated later if needed
							));
		}

		// Calculate percentages for PERCENTAGE routing type
		calculatePercentages(results, routingRules);

		return results;
	}

	/** Calculate percentages for PERCENTAGE routing type */
	private void calculatePercentages(Map<UUID, RoutingCalculationResult> results, List<RoutingRuleDto> routingRules) {

		for (RoutingRuleDto rule : routingRules) {
			if (rule.getRoutingType() != null && rule.getRoutingType().name().equals("PERCENTAGE")) {

				// Calculate total amount for all PSPs in this rule
				BigDecimal totalRuleAmount = rule.getPsps().stream()
						.map(pspInfo -> results.getOrDefault(
								pspInfo.getPspId(), new RoutingCalculationResult(BigDecimal.ZERO, 0L, BigDecimal.ZERO)))
						.map(RoutingCalculationResult::totalAmount)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				if (totalRuleAmount.compareTo(BigDecimal.ZERO) > 0) {
					// Update percentages for each PSP in this rule
					for (var pspInfo : rule.getPsps()) {
						UUID pspId = pspInfo.getPspId();
						RoutingCalculationResult current = results.get(pspId);
						if (current != null) {
							BigDecimal percentage = current.totalAmount()
									.divide(totalRuleAmount, 4, java.math.RoundingMode.HALF_UP)
									.multiply(BigDecimal.valueOf(100));

							results.put(
									pspId,
									new RoutingCalculationResult(
											current.totalAmount(), current.transactionCount(), percentage));
						}
					}
				}
			}
		}
	}

	@Override
	public LocalDateTime getStartTimeForDuration(RoutingDuration duration, LocalDateTime currentTime) {
		if (duration == null) {
			return currentTime.minusDays(1); // Default to 1 day
		}

		return switch (duration) {
			case HOUR -> currentTime.minusHours(1);
			case DAY -> currentTime.minusDays(1);
			case WEEK -> currentTime.minusWeeks(1);
			case MONTH -> currentTime.minusMonths(1);
		};
	}

	@Override
	public LocalDateTime getEndTimeForDuration(LocalDateTime currentTime) {
		return currentTime;
	}
}
