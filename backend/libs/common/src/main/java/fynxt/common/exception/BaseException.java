package fynxt.common.exception;

import fynxt.common.enums.ErrorCode;

import java.util.List;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

	private final ErrorCode errorCode;
	private final ErrorCategory category;
	private final String detail;
	private final List<ErrorDetail> errors;

	protected BaseException(
			String message, ErrorCode errorCode, ErrorCategory category, String detail, List<ErrorDetail> errors) {
		super(message);
		this.errorCode = errorCode;
		this.category = category;
		this.detail = detail;
		this.errors = errors != null ? errors : List.of();
	}

	protected BaseException(String message, ErrorCode errorCode, ErrorCategory category, String detail) {
		this(message, errorCode, category, detail, List.of());
	}

	protected BaseException(String message, ErrorCode errorCode, ErrorCategory category) {
		this(message, errorCode, category, errorCode.getMessage());
	}

	protected BaseException(String message, ErrorCode errorCode) {
		this(message, errorCode, ErrorCategory.INTERNAL);
	}

	public ErrorCode errorCode() {
		return errorCode;
	}

	public ErrorCategory category() {
		return category;
	}

	public HttpStatus httpStatus() {
		return category.http();
	}

	public String detail() {
		return detail;
	}

	public List<ErrorDetail> errors() {
		return errors;
	}
}
