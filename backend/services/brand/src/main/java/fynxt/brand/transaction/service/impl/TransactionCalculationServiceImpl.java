package fynxt.brand.transaction.service.impl;

import fynxt.brand.riskrule.enums.RiskDuration;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.brand.transaction.repository.TransactionRepository;
import fynxt.brand.transaction.service.TransactionCalculationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionCalculationServiceImpl implements TransactionCalculationService {

	private final TransactionRepository transactionRepository;

	@Override
	public BigDecimal calculateTotalAmount(
			UUID pspId,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime) {
		try {
			List<Transaction> transactions = transactionRepository.findByPspContext(
					pspId, brandId, environmentId, flowActionId, currency, startTime, endTime);

			BigDecimal totalAmount = transactions.stream()
					.filter(tx -> TransactionStatus.SUCCESS.equals(tx.getStatus()))
					.map(Transaction::getTxnAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			return totalAmount;
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal calculateTotalAmount(
			UUID pspId,
			UUID brandId,
			UUID environmentId,
			String customerId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime) {
		try {
			List<Transaction> transactions = transactionRepository.findByPspCustomerContext(
					pspId, customerId, brandId, environmentId, flowActionId, currency, startTime, endTime);

			BigDecimal totalAmount = transactions.stream()
					.filter(tx -> TransactionStatus.SUCCESS.equals(tx.getStatus()))
					.map(Transaction::getTxnAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			return totalAmount;
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal calculateCustomerTotalAmount(
			String customerId,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime) {
		try {
			List<Transaction> transactions = transactionRepository.findByCustomerContext(
					customerId, brandId, environmentId, flowActionId, currency, startTime, endTime);

			BigDecimal totalAmount = transactions.stream()
					.filter(tx -> TransactionStatus.SUCCESS.equals(tx.getStatus()))
					.map(Transaction::getTxnAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			return totalAmount;
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal calculateCustomerTotalAmount(
			String customerTag,
			String customerAccountType,
			UUID brandId,
			UUID environmentId,
			String flowActionId,
			String currency,
			LocalDateTime startTime,
			LocalDateTime endTime) {
		try {
			List<Transaction> transactions;

			if (customerTag != null && customerAccountType != null) {
				transactions = transactionRepository.findByCustomerCriteria(
						customerTag,
						customerAccountType,
						brandId,
						environmentId,
						flowActionId,
						currency,
						startTime,
						endTime);
			} else if (customerTag != null) {
				transactions = transactionRepository.findByCustomerTag(
						customerTag, brandId, environmentId, flowActionId, currency, startTime, endTime);
			} else if (customerAccountType != null) {
				transactions = transactionRepository.findByCustomerAccountType(
						customerAccountType, brandId, environmentId, flowActionId, currency, startTime, endTime);
			} else {
				return BigDecimal.ZERO;
			}

			BigDecimal totalAmount = transactions.stream()
					.filter(tx -> TransactionStatus.SUCCESS.equals(tx.getStatus()))
					.map(Transaction::getTxnAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			return totalAmount;
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public LocalDateTime getStartTimeForDuration(RiskDuration duration, LocalDateTime currentTime) {
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
