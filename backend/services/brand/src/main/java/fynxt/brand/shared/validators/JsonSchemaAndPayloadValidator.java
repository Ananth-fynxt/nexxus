package fynxt.brand.shared.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaAndPayloadValidator {

	private final ObjectMapper objectMapper;
	private final JsonSchemaFactory schemaFactory;

	public JsonSchemaAndPayloadValidator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
	}

	public ValidationResult validate(String schemaJson, String payload) {
		try {
			JsonNode schemaNode = objectMapper.readTree(schemaJson);
			JsonNode payloadNode = objectMapper.readTree(payload);

			JsonSchema schema = schemaFactory.getSchema(schemaNode);
			Set<ValidationMessage> validationMessages = schema.validate(payloadNode);

			if (validationMessages.isEmpty()) {
				return ValidationResult.success();
			} else {
				List<String> errors = new ArrayList<>();
				for (ValidationMessage message : validationMessages) {
					errors.add(message.getMessage());
				}
				return ValidationResult.failure(errors);
			}
		} catch (IOException e) {
			return ValidationResult.failure(List.of("Invalid JSON format: " + e.getMessage()));
		} catch (Exception e) {
			return ValidationResult.failure(List.of("Invalid JSON schema: " + e.getMessage()));
		}
	}

	public void validateAndThrow(String schemaJson, String payload) throws SchemaValidationException {
		ValidationResult result = validate(schemaJson, payload);
		if (!result.valid()) {
			throw new SchemaValidationException("Schema validation failed", result.errors());
		}
	}
}
