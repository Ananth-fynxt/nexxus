package fynxt.brand.psp.service.filter.fee;

import fynxt.brand.fee.dto.FeeComponentDto;
import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.PspOperationsService;
import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.request.dto.RequestOutputDto;
import fynxt.flowtarget.dto.FlowTargetDto;
import fynxt.flowtarget.service.FlowTargetService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeCalculationService {

	private final PspOperationsService pspOperationsService;
	private final FlowTargetService flowTargetService;

	public List<RequestOutputDto.PspInfo> calculateFeesForPsps(
			List<Psp> filteredPsps, List<FeeDto> feeRules, RequestInputDto request) {

		return buildPspInfoList(filteredPsps, feeRules, request);
	}

	private List<RequestOutputDto.PspInfo> buildPspInfoList(
			List<Psp> filteredPsps, List<FeeDto> feeRules, RequestInputDto request) {
		List<String> flowTargetIds = filteredPsps.stream()
				.map(Psp::getFlowTargetId)
				.filter(flowTargetId ->
						flowTargetId != null && !flowTargetId.trim().isEmpty())
				.distinct()
				.collect(Collectors.toList());

		Map<String, FlowTargetDto> flowTargetsMap = flowTargetIds.isEmpty()
				? Map.of()
				: flowTargetService.readByIds(flowTargetIds).stream()
						.collect(Collectors.toMap(FlowTargetDto::getId, Function.identity()));

		return filteredPsps.stream()
				.map(psp -> calculateFeeForPsp(psp, feeRules, request, flowTargetsMap))
				.collect(Collectors.toList());
	}

	private RequestOutputDto.PspInfo calculateFeeForPsp(
			Psp psp, List<FeeDto> feeRules, RequestInputDto request, Map<String, FlowTargetDto> flowTargetsMap) {

		BigDecimal originalAmount = request.getAmount();

		// Filter fees that are applicable to this specific PSP
		List<FeeDto> applicableFees = filterFeesForPsp(feeRules, psp.getId());

		// Calculate fees with proper INCLUSIVE/EXCLUSIVE separation
		FeeCalculationResult feeResult = calculateFeesWithTracking(applicableFees, request);

		// Calculate amounts based on fee types
		BigDecimal totalAmount = originalAmount.add(feeResult.getExclusiveFeeAmount());
		BigDecimal netAmountToUser = originalAmount.subtract(feeResult.getInclusiveFeeAmount());
		boolean isFeeApplied = feeResult.getTotalFeeAmount().compareTo(BigDecimal.ZERO) > 0;

		String flowDefinitionId = pspOperationsService.fetchFlowDefinitionId(psp.getId(), request.getActionId());

		RequestOutputDto.FlowTargetData flowTargetData = null;
		if (psp.getFlowTargetId() != null && flowTargetsMap.containsKey(psp.getFlowTargetId())) {
			FlowTargetDto flowTargetDto = flowTargetsMap.get(psp.getFlowTargetId());
			flowTargetData = buildFlowTargetData(flowTargetDto);
		}

		RequestOutputDto.PspInfo result = RequestOutputDto.PspInfo.builder()
				.id(psp.getId())
				.name(psp.getName())
				.description(psp.getDescription())
				.logo(psp.getLogo())
				.brandId(psp.getBrandId())
				.environmentId(psp.getEnvironmentId())
				.flowActionId(request.getActionId())
				.flowDefintionId(flowDefinitionId)
				.currency(request.getCurrency())
				.originalAmount(originalAmount)
				.appliedFeeAmount(isFeeApplied ? feeResult.getTotalFeeAmount() : null)
				.totalAmount(totalAmount)
				.netAmountToUser(isFeeApplied ? netAmountToUser : null)
				.inclusiveFeeAmount(isFeeApplied ? feeResult.getInclusiveFeeAmount() : null)
				.exclusiveFeeAmount(isFeeApplied ? feeResult.getExclusiveFeeAmount() : null)
				.isFeeApplied(isFeeApplied)
				.flowTarget(flowTargetData)
				.build();

		return result;
	}

	private BigDecimal calculateComponentAmount(FeeComponentDto component, RequestInputDto request) {
		BigDecimal transactionAmount = request.getAmount();
		if (transactionAmount == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal calculatedAmount = BigDecimal.ZERO;

		switch (component.getType()) {
			case FIXED:
				calculatedAmount = component.getAmount();
				break;
			case PERCENTAGE:
				calculatedAmount =
						transactionAmount.multiply(component.getAmount()).divide(BigDecimal.valueOf(100));
				calculatedAmount = applyMinMaxLimits(calculatedAmount, component);
				break;
			default:
				return BigDecimal.ZERO;
		}

		return calculatedAmount;
	}

	private BigDecimal applyMinMaxLimits(BigDecimal calculatedAmount, FeeComponentDto component) {
		BigDecimal minValue = component.getMinValue();
		BigDecimal maxValue = component.getMaxValue();

		if (minValue != null && calculatedAmount.compareTo(minValue) < 0) {
			calculatedAmount = minValue;
		}

		if (maxValue != null && calculatedAmount.compareTo(maxValue) > 0) {
			calculatedAmount = maxValue;
		}

		return calculatedAmount;
	}

	private RequestOutputDto.FlowTargetData buildFlowTargetData(FlowTargetDto flowTargetDto) {
		return RequestOutputDto.FlowTargetData.builder()
				.flowTargetId(flowTargetDto.getId())
				.inputSchema(flowTargetDto.getInputSchema())
				.build();
	}

	private List<FeeDto> filterFeesForPsp(List<FeeDto> feeRules, UUID pspId) {
		if (CollectionUtils.isEmpty(feeRules)) {
			return List.of();
		}

		return feeRules.stream()
				.filter(fee -> isPspApplicableForFee(fee, pspId))
				.collect(Collectors.toList());
	}

	private boolean isPspApplicableForFee(FeeDto fee, UUID pspId) {
		if (CollectionUtils.isEmpty(fee.getPsps())) {
			return false; // No PSPs configured for this fee, skip it
		}

		String pspIdStr = pspId != null ? pspId.toString() : null;
		return fee.getPsps().stream().anyMatch(psp -> pspIdStr != null && pspIdStr.equals(psp.getId()));
	}

	private FeeCalculationResult calculateFeesWithTracking(List<FeeDto> applicableFees, RequestInputDto request) {
		if (CollectionUtils.isEmpty(applicableFees)) {
			return FeeCalculationResult.builder()
					.totalFeeAmount(BigDecimal.ZERO)
					.inclusiveFeeAmount(BigDecimal.ZERO)
					.exclusiveFeeAmount(BigDecimal.ZERO)
					.build();
		}

		BigDecimal totalInclusiveFee = BigDecimal.ZERO;
		BigDecimal totalExclusiveFee = BigDecimal.ZERO;

		for (FeeDto fee : applicableFees) {
			BigDecimal feeAmount = BigDecimal.ZERO;

			for (FeeComponentDto component : fee.getComponents()) {
				BigDecimal componentAmount = calculateComponentAmount(component, request);
				feeAmount = feeAmount.add(componentAmount);
			}

			if (fee.getChargeFeeType() == fynxt.brand.fee.enums.ChargeFeeType.INCLUSIVE) {
				totalInclusiveFee = totalInclusiveFee.add(feeAmount);
			} else {
				totalExclusiveFee = totalExclusiveFee.add(feeAmount);
			}
		}

		BigDecimal totalFeeAmount = totalInclusiveFee.add(totalExclusiveFee);

		return FeeCalculationResult.builder()
				.totalFeeAmount(totalFeeAmount)
				.inclusiveFeeAmount(totalInclusiveFee)
				.exclusiveFeeAmount(totalExclusiveFee)
				.build();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class FeeCalculationResult {
		private final BigDecimal totalFeeAmount;
		private final BigDecimal inclusiveFeeAmount;
		private final BigDecimal exclusiveFeeAmount;
	}
}
