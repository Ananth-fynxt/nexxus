package fynxt.common.exception;

import fynxt.common.enums.ErrorCode;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;

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

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {
		return responseBuilder.error(ex.errorCode(), ex.detail(), ex.httpStatus(), ex.errors());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		return responseBuilder.error(ErrorCode.GENERIC_ERROR, ex.getReason(), status, null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
		return responseBuilder.error(ErrorCode.GENERIC_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
	}
}
