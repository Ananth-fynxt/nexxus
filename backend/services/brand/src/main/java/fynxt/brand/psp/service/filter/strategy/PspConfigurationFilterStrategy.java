package fynxt.brand.psp.service.filter.strategy;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.AccessValidationService;
import fynxt.brand.psp.service.FailureRateValidationService;
import fynxt.brand.psp.service.IpValidationService;
import fynxt.brand.psp.service.MaintenanceWindowService;
import fynxt.brand.psp.service.PspOperationValidationService;
import fynxt.brand.psp.service.filter.PspFilterContext;
import fynxt.brand.psp.service.filter.PspFilterStrategy;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PspConfigurationFilterStrategy implements PspFilterStrategy {

	private final MaintenanceWindowService maintenanceWindowService;
	private final PspOperationValidationService pspOperationValidationService;
	private final IpValidationService ipValidationService;
	private final AccessValidationService accessValidationService;
	private final FailureRateValidationService failureRateValidationService;

	@Override
	public PspFilterContext apply(PspFilterContext context) {
		List<Psp> currentPsps = context.getFilteredPsps();

		List<Psp> filteredPsps = applyFilters(currentPsps, context.getRequest());

		context.updateFilteredPsps(filteredPsps);
		context.addFilterMetadata("psp_configuration_filtered_count", currentPsps.size() - filteredPsps.size());

		return context;
	}

	private List<Psp> applyFilters(List<Psp> psps, RequestInputDto request) {
		List<Psp> filteredPsps = psps;

		filteredPsps = maintenanceWindowService.filterPspsNotInMaintenance(filteredPsps, request.getActionId());
		if (filteredPsps.isEmpty()) return filteredPsps;

		filteredPsps = pspOperationValidationService.filterValidPspOperations(filteredPsps, request);
		if (filteredPsps.isEmpty()) return filteredPsps;

		filteredPsps = ipValidationService.filterValidIps(filteredPsps, request);
		if (filteredPsps.isEmpty()) return filteredPsps;

		filteredPsps = accessValidationService.filterValidAccess(filteredPsps, request);
		if (filteredPsps.isEmpty()) return filteredPsps;

		filteredPsps = failureRateValidationService.filterValidFailureRates(filteredPsps, request);

		return filteredPsps;
	}

	@Override
	public int getPriority() {
		return 3; // Third priority - apply after risk rules and transaction limits
	}

	@Override
	public String getStrategyName() {
		return "PspConfigurationFilter";
	}

	@Override
	public boolean shouldApply(PspFilterContext context) {
		return true;
	}
}
