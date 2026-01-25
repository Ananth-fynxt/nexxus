package fynxt.common.exception;

public record ErrorDetail(String object, String field, String message, Object rejectedValue) {

	public ErrorDetail(String field, String message) {
		this(null, field, message, null);
	}

	public ErrorDetail(String message) {
		this(null, null, message, null);
	}
}
