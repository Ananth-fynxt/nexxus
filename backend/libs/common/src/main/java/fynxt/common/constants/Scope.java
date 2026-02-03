package fynxt.common.constants;

public enum Scope {
	FI("FI"),
	BRAND("BRAND");

	private final String value;

	Scope(String value) {
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
