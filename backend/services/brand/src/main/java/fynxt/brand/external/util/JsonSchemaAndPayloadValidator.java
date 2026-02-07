package fynxt.brand.external.util;

import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.AppException;
import fynxt.common.exception.ErrorCategory;

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

	public void validateAndThrow(String schemaJson, String payload) throws AppException {
		List<String> errors = collectValidationErrors(schemaJson, payload);
		if (!errors.isEmpty()) {
			String message = "Schema validation failed: " + String.join("; ", errors);
			throw new AppException(message, ErrorCode.VALIDATION_ERROR, ErrorCategory.BAD_REQUEST);
		}
	}

	private List<String> collectValidationErrors(String schemaJson, String payload) {
		try {
			JsonNode schemaNode = objectMapper.readTree(schemaJson);
			JsonNode payloadNode = objectMapper.readTree(payload);

			JsonSchema schema = schemaFactory.getSchema(schemaNode);
			Set<ValidationMessage> validationMessages = schema.validate(payloadNode);

			if (validationMessages.isEmpty()) {
				return List.of();
			}
			List<String> errors = new ArrayList<>();
			for (ValidationMessage message : validationMessages) {
				errors.add(message.getMessage());
			}
			return errors;
		} catch (IOException e) {
			return List.of("Invalid JSON format: " + e.getMessage());
		} catch (Exception e) {
			return List.of("Invalid JSON schema: " + e.getMessage());
		}
	}
}
