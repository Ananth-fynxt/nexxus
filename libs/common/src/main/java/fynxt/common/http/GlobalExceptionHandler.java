package fynxt.common.http;

import fynxt.common.constants.ErrorCode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final ResponseBuilder responseBuilder;

	public GlobalExceptionHandler(ResponseBuilder responseBuilder) {
		this.responseBuilder = responseBuilder;
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
		ErrorCode errorCode = resolveErrorCode(ex.getReason());
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		return responseBuilder.error(errorCode, ex.getReason(), status);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
		return responseBuilder.error(ErrorCode.GENERIC_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected ErrorCode resolveErrorCode(String code) {
		if (code == null) {
			return ErrorCode.GENERIC_ERROR;
		}
		for (ErrorCode errorCode : ErrorCode.values()) {
			if (errorCode.getCode().equals(code)) {
				return errorCode;
			}
		}
		return ErrorCode.GENERIC_ERROR;
	}
}
