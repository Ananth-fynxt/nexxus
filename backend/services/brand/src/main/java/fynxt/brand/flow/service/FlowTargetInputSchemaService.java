package fynxt.brand.flow.service;

import java.util.List;

public interface FlowTargetInputSchemaService {

	List<String> extractCurrencies(Object inputSchema);

	List<String> extractCountries(Object inputSchema);

	List<String> extractPaymentMethods(Object inputSchema);

	boolean validateCurrency(String currency, Object inputSchema);

	void validateCurrencies(List<String> currencies, Object inputSchema);

	void validateCountries(List<String> countries, Object inputSchema);
}
