package fynxt.brand.psp.service.impl;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.entity.PspOperation;
import fynxt.brand.psp.repository.PspOperationRepository;
import fynxt.brand.psp.service.PspOperationValidationService;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class PspOperationValidationServiceImpl implements PspOperationValidationService {

	private final PspOperationRepository pspOperationRepository;

	@Override
	public boolean isPspOperationValid(Psp psp, RequestInputDto request) {
		List<PspOperation> operations = pspOperationRepository.findByPspId(psp.getId());

		if (CollectionUtils.isEmpty(operations)) {
			return false;
		}

		String requestCurrency = request.getCurrency();
		String requestCountry = request.getCountry();
		String requestActionId = request.getActionId();

		return operations.stream()
				.anyMatch(operation ->
						matchesOperationCriteria(operation, requestCurrency, requestCountry, requestActionId));
	}

	@Override
	public List<Psp> filterValidPspOperations(List<Psp> psps, RequestInputDto request) {
		return psps.stream().filter(psp -> isPspOperationValid(psp, request)).collect(Collectors.toList());
	}

	private boolean matchesOperationCriteria(PspOperation operation, String currency, String country, String actionId) {
		return matchesCurrency(operation, currency)
				&& matchesCountry(operation, country)
				&& matchesFlowAction(operation, actionId);
	}

	private boolean matchesCurrency(PspOperation operation, String requestCurrency) {
		if (requestCurrency == null) {
			return true;
		}
		return operation.getCurrencies() != null && operation.getCurrencies().contains(requestCurrency);
	}

	private boolean matchesCountry(PspOperation operation, String requestCountry) {
		if (requestCountry == null) {
			return true; // Country is optional, skip validation if not provided
		}
		// Only validate if PSP operation has countries configured
		if (operation.getCountries() == null || operation.getCountries().isEmpty()) {
			return true;
		}
		return operation.getCountries().contains(requestCountry);
	}

	private boolean matchesFlowAction(PspOperation operation, String requestActionId) {
		if (requestActionId == null) {
			return false;
		}
		return requestActionId.equals(operation.getFlowActionId());
	}
}
