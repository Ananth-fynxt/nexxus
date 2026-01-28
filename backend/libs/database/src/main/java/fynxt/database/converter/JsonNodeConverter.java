package fynxt.database.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(JsonNode attribute) {
		if (attribute == null) {
			return null;
		}

		try {
			return MAPPER.writeValueAsString(attribute);
		} catch (JacksonException e) {
			throw new IllegalStateException("Failed to serialize JsonNode to database column", e);
		}
	}

	@Override
	public JsonNode convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}

		try {
			return MAPPER.readTree(dbData);
		} catch (JacksonException e) {
			throw new IllegalStateException("Failed to deserialize database column to JsonNode", e);
		}
	}
}
