package fynxt.mapper.json;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class JsonMapper {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public JsonNode toJsonNode(Object value) {
		if (value == null) {
			return null;
		}

		try {
			if (value instanceof JsonNode node) {
				return node;
			}
			if (value instanceof String json) {
				return MAPPER.readTree(json);
			}
			return MAPPER.valueToTree(value);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Failed to convert to JsonNode: " + value.getClass().getSimpleName(), e);
		}
	}

	public JsonNode fromString(String json) {
		if (json == null) {
			return null;
		}

		try {
			return MAPPER.readTree(json);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JSON string", e);
		}
	}

	public String toString(JsonNode node) {
		return node != null ? node.toString() : null;
	}

	public String toJsonString(Object value) {
		if (value == null) {
			return null;
		}

		try {
			return MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to serialize to JSON string", e);
		}
	}

	public <T> T fromString(String json, Class<T> clazz) {
		if (json == null) {
			return null;
		}

		try {
			return MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to deserialize JSON to " + clazz.getSimpleName(), e);
		}
	}
}
