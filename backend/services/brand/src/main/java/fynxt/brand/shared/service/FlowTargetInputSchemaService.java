package fynxt.brand.shared.service;

import fynxt.brand.shared.dto.ValidationResult;

import java.util.List;

public interface FlowTargetInputSchemaService {

	List<String> extractCurrencies(Object inputSchema);

	List<String> extractCountries(Object inputSchema);

	List<String> extractPaymentMethods(Object inputSchema);

	boolean validateCurrency(String currency, Object inputSchema);

	ValidationResult validateCurrencies(List<String> currencies, Object inputSchema);

	ValidationResult validateCountries(List<String> countries, Object inputSchema);
}
