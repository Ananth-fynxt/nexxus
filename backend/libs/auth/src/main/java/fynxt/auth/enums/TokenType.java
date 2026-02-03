package fynxt.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
	ACCESS("ACCESS"),
	REFRESH("REFRESH");

	@JsonValue
	private final String value;

	public static TokenType fromValue(String value) {
		for (TokenType type : TokenType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown TokenType: " + value);
	}
}
