package fynxt.common.http;

import fynxt.common.constants.ErrorCode;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ResponseBuilder {

	ResponseEntity<ApiResponse<Object>> success(Object data, String message);

	ResponseEntity<ApiResponse<Object>> success(Object data);

	ResponseEntity<ApiResponse<Object>> success(String message);

	<T> ResponseEntity<ApiResponse<Object>> paginated(Page<T> page, String message);

	ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, HttpStatus status);

	ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, String details, HttpStatus status);
}
