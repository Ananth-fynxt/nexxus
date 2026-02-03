package fynxt.brand.user.constants;

public enum UserStatus {
	ACTIVE("ACTIVE"),
	INACTIVE("INACTIVE"),
	SUSPENDED("SUSPENDED"),
	PENDING("PENDING");

	private final String value;

	UserStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
