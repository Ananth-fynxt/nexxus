package fynxt.brand.psp.service.resolution;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.repository.PspRepository;
import fynxt.brand.psp.service.filter.PspFilterContext;
import fynxt.brand.psp.service.filter.PspFilterPipeline;
import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.riskrule.enums.RiskAction;
import fynxt.brand.riskrule.service.RiskRuleService;
import fynxt.brand.routingrule.dto.RoutingRuleDto;
import fynxt.brand.routingrule.service.RoutingRuleService;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;
import fynxt.brand.transactionlimit.service.TransactionLimitService;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PspResolutionService {

	private final PspRepository pspRepository;
	private final PspFilterPipeline pspFilterPipeline;
	private final RiskRuleService riskRuleService;
	private final TransactionLimitService transactionLimitService;
	private final RoutingRuleService routingRuleService;

	public PspResolutionResult resolvePsps(RequestInputDto request) {
		var fetchResult = fetchGlobalPsps(request);
		List<Psp> globalPsps = fetchResult.psps();
		String fetchStrategy = fetchResult.strategy();

		if (globalPsps.isEmpty()) {
			return PspResolutionResult.builder()
					.filteredPsps(List.of())
					.globalPsps(List.of())
					.riskRules(List.of())
					.feeRules(List.of())
					.transactionLimits(List.of())
					.resolvedByStrategy("GlobalPspResolution")
					.usedRoutingRuleRefinement(false)
					.fetchStrategy("NONE")
					.build();
		}

		return applyCompleteFilterPipeline(request, globalPsps, fetchStrategy);
	}

	private FetchResult fetchGlobalPsps(RequestInputDto request) {
		List<Psp> psps = pspRepository.findActivePspsByBrandEnvironmentActionAndCurrency(
				request.getBrandId(), request.getEnvironmentId(), request.getActionId(), request.getCurrency());

		if (!psps.isEmpty()) {
			return new FetchResult(psps, "CURRENCY_ACTION");
		}

		psps = pspRepository.findActivePspsByBrandEnvironmentAndAction(
				request.getBrandId(), request.getEnvironmentId(), request.getActionId());

		if (!psps.isEmpty()) {
			return new FetchResult(psps, "ACTION_ONLY");
		}

		return new FetchResult(List.of(), "NONE");
	}

	private record FetchResult(List<Psp> psps, String strategy) {}

	private PspResolutionResult applyCompleteFilterPipeline(
			RequestInputDto request, List<Psp> psps, String fetchStrategy) {

		List<UUID> pspIds = psps.stream().map(Psp::getId).collect(Collectors.toList());

		List<RiskRuleDto> riskRules = riskRuleService.readLatestEnabledRiskRulesByCriteria(
				pspIds,
				request.getBrandId(),
				request.getEnvironmentId(),
				request.getActionId(),
				request.getCurrency(),
				RiskAction.BLOCK,
				Status.ENABLED);

		List<TransactionLimitDto> transactionLimits =
				transactionLimitService.readLatestEnabledTransactionLimitsByCriteria(
						pspIds,
						request.getBrandId(),
						request.getEnvironmentId(),
						request.getActionId(),
						request.getCurrency(),
						Status.ENABLED);

		List<RoutingRuleDto> routingRules = routingRuleService.findEnabledRoutingRulesByBrandAndEnvironment(
				request.getBrandId(), request.getEnvironmentId());

		PspFilterContext filterContext = PspFilterContext.initialize(request, psps);
		filterContext.setRiskRules(riskRules);
		filterContext.setTransactionLimits(transactionLimits);
		filterContext.setRoutingRules(routingRules);
		filterContext.setFeeRules(List.of());

		PspFilterContext filteredContext = pspFilterPipeline.applyFilters(filterContext);

		return PspResolutionResult.builder()
				.filteredPsps(filteredContext.getFilteredPsps())
				.globalPsps(psps)
				.riskRules(riskRules)
				.feeRules(List.of())
				.transactionLimits(transactionLimits)
				.resolvedByStrategy("CompleteFilterPipeline")
				.usedRoutingRuleRefinement(!routingRules.isEmpty())
				.fetchStrategy(fetchStrategy)
				.build();
	}
}
