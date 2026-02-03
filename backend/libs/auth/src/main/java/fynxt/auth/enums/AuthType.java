package fynxt.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {
	APPLICATION_USER("APPLICATION_USER"),
	SECRET_TOKEN("SECRET_TOKEN"),
	ADMIN_TOKEN("ADMIN_TOKEN");

	@JsonValue
	private final String value;

	public static AuthType fromValue(String value) {
		for (AuthType type : AuthType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown AuthType: " + value);
	}
}
