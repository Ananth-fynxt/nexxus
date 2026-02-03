package fynxt.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenStatus {
	ACTIVE("ACTIVE"),
	EXPIRED("EXPIRED"),
	REVOKED("REVOKED");

	@JsonValue
	private final String value;

	public static TokenStatus fromValue(String value) {
		for (TokenStatus status : TokenStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Unknown TokenStatus: " + value);
	}
}
