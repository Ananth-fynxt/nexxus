package fynxt.brand.psp.service.filter;

import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PspFilterPipeline {

	private final List<PspFilterStrategy> filterStrategies;

	public PspFilterContext applyFilters(PspFilterContext context) {

		List<PspFilterStrategy> sortedStrategies = filterStrategies.stream()
				.filter(strategy -> strategy.shouldApply(context))
				.sorted(Comparator.comparing(PspFilterStrategy::getPriority))
				.toList();

		PspFilterContext currentContext = context;

		for (PspFilterStrategy strategy : sortedStrategies) {
			int beforeCount = currentContext.getFilteredPsps().size();

			currentContext = strategy.apply(currentContext);

			int afterCount = currentContext.getFilteredPsps().size();
			int filteredOut = beforeCount - afterCount;

			currentContext.addFilterMetadata(strategy.getStrategyName() + "_filtered_count", filteredOut);

			if (currentContext.getFilteredPsps().isEmpty()) {
				break;
			}
		}

		return currentContext;
	}
}
