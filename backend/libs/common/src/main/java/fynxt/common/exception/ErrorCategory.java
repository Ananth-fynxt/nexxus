package fynxt.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCategory {
	NOT_FOUND(HttpStatus.NOT_FOUND),
	BAD_REQUEST(HttpStatus.BAD_REQUEST),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
	FORBIDDEN(HttpStatus.FORBIDDEN),
	CONFLICT(HttpStatus.CONFLICT),
	DUPLICATE(HttpStatus.CONFLICT),
	INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR);

	private final HttpStatus http;

	ErrorCategory(HttpStatus http) {
		this.http = http;
	}

	public HttpStatus http() {
		return http;
	}
}
