package fynxt.brand.request.service.impl;

import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.fee.service.FeeService;
import fynxt.brand.psp.service.filter.fee.FeeCalculationService;
import fynxt.brand.psp.service.resolution.PspResolutionResult;
import fynxt.brand.psp.service.resolution.PspResolutionService;
import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.request.dto.RequestOutputDto;
import fynxt.brand.request.entity.Request;
import fynxt.brand.request.entity.RequestFee;
import fynxt.brand.request.entity.RequestPsp;
import fynxt.brand.request.entity.RequestRiskRule;
import fynxt.brand.request.entity.RequestTransactionLimit;
import fynxt.brand.request.repository.RequestFeeRepository;
import fynxt.brand.request.repository.RequestPspRepository;
import fynxt.brand.request.repository.RequestRepository;
import fynxt.brand.request.repository.RequestRiskRuleRepository;
import fynxt.brand.request.repository.RequestTransactionLimitRepository;
import fynxt.brand.request.service.RequestService;
import fynxt.brand.request.service.mappers.RequestFeeMapper;
import fynxt.brand.request.service.mappers.RequestMapper;
import fynxt.brand.request.service.mappers.RequestPspMapper;
import fynxt.brand.request.service.mappers.RequestRiskRuleMapper;
import fynxt.brand.request.service.mappers.RequestTransactionLimitMapper;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestRepository requestRepository;
	private final RequestPspRepository requestPspRepository;
	private final RequestFeeRepository requestFeeRepository;
	private final RequestRiskRuleRepository requestRiskRuleRepository;
	private final RequestTransactionLimitRepository requestTransactionLimitRepository;
	private final RequestMapper requestMapper;
	private final RequestPspMapper requestPspMapper;
	private final RequestFeeMapper requestFeeMapper;
	private final RequestRiskRuleMapper requestRiskRuleMapper;
	private final RequestTransactionLimitMapper requestTransactionLimitMapper;
	private final PspResolutionService pspResolutionService;
	private final FeeCalculationService feeCalculationService;
	private final FeeService feeService;

	@Override
	@Transactional
	public RequestOutputDto fetchPsp(@Valid RequestInputDto requestInputDto) {
		// Step 1: Create and save Request entity using mapper
		Request request = requestMapper.toRequest(requestInputDto);
		Request savedRequest = requestRepository.save(request);

		// Step 2: Resolve PSPs (fetch + filter - WITHOUT fee calculation for performance)
		PspResolutionResult resolutionResult = pspResolutionService.resolvePsps(requestInputDto);

		// Step 3: Load fee rules ONLY ONCE for the final filtered PSPs (performance optimization)
		List<FeeDto> feeRules = loadFeeRulesForFinalPsps(requestInputDto, resolutionResult);

		// Step 4: Calculate fees ONLY ONCE on final PSP list (minimal effort)
		List<RequestOutputDto.PspInfo> pspsWithFees = feeCalculationService.calculateFeesForPsps(
				resolutionResult.getFilteredPsps(), feeRules, requestInputDto);

		// Step 5: Save RequestPsp entities using mapper and create associations
		createPsps(savedRequest, pspsWithFees);

		// Step 6: Save related entities (fees, risk rules, transaction limits)
		saveRelatedEntities(
				savedRequest.getId(),
				resolutionResult.getRiskRules(),
				feeRules,
				resolutionResult.getTransactionLimits());

		// Step 7: Build response - return PSPs data with requestId
		RequestOutputDto responseDto = RequestOutputDto.builder()
				.requestId(savedRequest.getId())
				.psps(pspsWithFees)
				.build();

		return responseDto;
	}

	/** Load fee rules ONLY for the final filtered PSPs */
	private List<FeeDto> loadFeeRulesForFinalPsps(RequestInputDto request, PspResolutionResult resolutionResult) {
		if (resolutionResult.getFilteredPsps().isEmpty()) {
			return List.of();
		}

		List<UUID> pspIds = resolutionResult.getFilteredPsps().stream()
				.map(fynxt.brand.psp.entity.Psp::getId)
				.collect(Collectors.toList());

		return feeService.readLatestEnabledFeeRulesByCriteria(
				pspIds,
				request.getBrandId(),
				request.getEnvironmentId(),
				request.getActionId(),
				request.getCurrency(),
				fynxt.common.enums.Status.ENABLED);
	}

	private void createPsps(Request request, List<RequestOutputDto.PspInfo> pspInfos) {
		if (pspInfos != null && !pspInfos.isEmpty()) {
			List<RequestPsp> requestPsps = pspInfos.stream()
					.map(pspInfo -> requestPspMapper.toRequestPsp(request.getId(), pspInfo))
					.collect(Collectors.toList());
			requestPspRepository.saveAll(requestPsps);
		}
	}

	private void saveRelatedEntities(
			UUID requestId,
			List<RiskRuleDto> riskRules,
			List<FeeDto> feeRules,
			List<TransactionLimitDto> transactionLimits) {

		// Save Risk Rules
		if (riskRules != null && !riskRules.isEmpty()) {
			List<RequestRiskRule> requestRiskRules = riskRules.stream()
					.map(riskRule -> requestRiskRuleMapper.toRequestRiskRule(requestId, riskRule))
					.collect(Collectors.toList());
			requestRiskRuleRepository.saveAll(requestRiskRules);
		}

		// Save Fee Rules
		if (feeRules != null && !feeRules.isEmpty()) {
			List<RequestFee> requestFees = feeRules.stream()
					.map(feeRule -> requestFeeMapper.toRequestFee(requestId, feeRule))
					.collect(Collectors.toList());
			requestFeeRepository.saveAll(requestFees);
		}

		// Save Transaction Limits
		if (transactionLimits != null && !transactionLimits.isEmpty()) {
			List<RequestTransactionLimit> requestTransactionLimits = transactionLimits.stream()
					.map(transactionLimit ->
							requestTransactionLimitMapper.toRequestTransactionLimit(requestId, transactionLimit))
					.collect(Collectors.toList());
			requestTransactionLimitRepository.saveAll(requestTransactionLimits);
		}
	}

	@Override
	public RequestService.CustomerInfo getCustomerInfoByRequestId(UUID requestId) {
		Request request = requestRepository
				.findById(requestId)
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found with id: " + requestId));
		return new RequestService.CustomerInfo(
				request.getCustomerId(), request.getCustomerTag(), request.getCustomerAccountType());
	}
}
