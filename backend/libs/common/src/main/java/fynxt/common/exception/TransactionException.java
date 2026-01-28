package fynxt.common.exception;

import fynxt.common.constants.ErrorCode;

import java.util.List;

public class TransactionException extends BaseException {

	public TransactionException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public TransactionException(String message, ErrorCode errorCode, ErrorCategory category) {
		super(message, errorCode, category);
	}

	public TransactionException(String message, ErrorCode errorCode, ErrorCategory category, String detail) {
		super(message, errorCode, category, detail);
	}

	public TransactionException(
			String message, ErrorCode errorCode, ErrorCategory category, String detail, List<ErrorDetail> errors) {
		super(message, errorCode, category, detail, errors);
	}
}
