package fynxt.brand.psp.service.impl;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.FailureRateValidationService;
import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.transaction.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailureRateValidationServiceImpl implements FailureRateValidationService {

	private final TransactionService transactionService;

	@Override
	public boolean isFailureRateValid(Psp psp, RequestInputDto request) {
		if (!Boolean.TRUE.equals(psp.getFailureRate())) {
			return true;
		}

		Float failureRateThreshold = psp.getFailureRateThreshold();
		Integer failureRateDurationMinutes = psp.getFailureRateDurationMinutes();

		if (failureRateThreshold == null || failureRateDurationMinutes == null) {
			return true;
		}

		try {
			String requestFlowActionId = request.getActionId();
			String customerId = request.getCustomerId();
			LocalDateTime endTime = LocalDateTime.now();
			LocalDateTime startTime = endTime.minusMinutes(failureRateDurationMinutes);

			double currentFailureRate = transactionService.calculateFailureRateByCustomer(
					psp.getId(), customerId, requestFlowActionId, startTime, endTime);

			if (currentFailureRate > failureRateThreshold) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public List<Psp> filterValidFailureRates(List<Psp> psps, RequestInputDto request) {
		return psps.stream().filter(psp -> isFailureRateValid(psp, request)).collect(Collectors.toList());
	}
}
