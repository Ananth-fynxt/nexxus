package fynxt.brand.shared.service.impl;

import fynxt.brand.shared.dto.ValidationResult;
import fynxt.brand.shared.service.FlowTargetInputSchemaService;
import fynxt.common.http.ApiResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlowTargetInputSchemaServiceImpl implements FlowTargetInputSchemaService {

	private final ObjectMapper objectMapper;

	private List<String> extractEnumArray(Object inputSchema, String fieldName) {
		JsonNode schemaNode = getInputSchemaNode(inputSchema);
		if (schemaNode == null || !schemaNode.has(fieldName)) {
			return List.of();
		}

		JsonNode fieldNode = schemaNode.get(fieldName);
		JsonNode itemsNode = fieldNode.path("items");
		JsonNode enumNode = itemsNode.path("enum");

		if (!enumNode.isArray()) {
			return List.of();
		}

		return StreamSupport.stream(enumNode.spliterator(), false)
				.filter(JsonNode::isTextual)
				.map(JsonNode::asText)
				.collect(Collectors.toList());
	}

	private JsonNode getInputSchemaNode(Object inputSchema) {
		if (inputSchema == null) {
			return null;
		}
		if (inputSchema instanceof JsonNode) {
			return (JsonNode) inputSchema;
		}
		if (inputSchema instanceof String) {
			try {
				return objectMapper.readTree((String) inputSchema);
			} catch (JsonProcessingException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public List<String> extractCurrencies(Object inputSchema) {
		return extractEnumArray(inputSchema, "currencies");
	}

	@Override
	public List<String> extractCountries(Object inputSchema) {
		return extractEnumArray(inputSchema, "countries");
	}

	@Override
	public List<String> extractPaymentMethods(Object inputSchema) {
		return extractEnumArray(inputSchema, "paymentMethods");
	}

	@Override
	public boolean validateCurrency(String currency, Object inputSchema) {
		return currency != null
				&& !currency.isBlank()
				&& extractCurrencies(inputSchema).contains(currency);
	}

	private ValidationResult validateEnumList(
			List<String> values, Object inputSchema, String fieldName, String errorCode, String fieldDisplayName) {
		if (values == null || values.isEmpty()) {
			return ValidationResult.success();
		}

		List<String> supportedValues = extractEnumArray(inputSchema, fieldName);
		if (supportedValues.isEmpty()) {
			return ValidationResult.success();
		}

		List<String> invalidValues = values.stream()
				.filter(v -> v != null && !v.isBlank() && !supportedValues.contains(v))
				.collect(Collectors.toList());

		if (!invalidValues.isEmpty()) {
			String errorMessage = String.format(
					"Invalid %s: %s. Supported %s: %s",
					fieldDisplayName,
					String.join(", ", invalidValues),
					fieldDisplayName,
					String.join(", ", supportedValues));
			return ValidationResult.failure(ResponseEntity.badRequest()
					.body(ApiResponse.builder()
							.timestamp(OffsetDateTime.now(ZoneOffset.UTC))
							.code(errorCode)
							.message(errorMessage)
							.build()));
		}

		return ValidationResult.success();
	}

	@Override
	public ValidationResult validateCurrencies(List<String> currencies, Object inputSchema) {
		return validateEnumList(currencies, inputSchema, "currencies", "INVALID_CURRENCY", "currencies");
	}

	@Override
	public ValidationResult validateCountries(List<String> countries, Object inputSchema) {
		return validateEnumList(countries, inputSchema, "countries", "INVALID_COUNTRY", "countries");
	}
}
