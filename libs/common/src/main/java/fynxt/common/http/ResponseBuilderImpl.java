package fynxt.common.http;

import fynxt.common.constants.ErrorCode;
import fynxt.common.exception.ErrorDetail;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilderImpl implements ResponseBuilder {

	private static final String SUCCESS_CODE = ErrorCode.SUCCESS.getCode();

	@Override
	public ResponseEntity<ApiResponse<Object>> success(Object data, String message) {
		return ResponseEntity.ok(ApiResponse.builder()
				.timestamp(now())
				.code(SUCCESS_CODE)
				.message(message)
				.data(data)
				.build());
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> success(Object data) {
		return success(data, "Operation completed successfully");
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> success(String message) {
		return success(null, message);
	}

	@Override
	public <T> ResponseEntity<ApiResponse<Object>> paginated(Page<T> page, String message) {
		ApiResponse.PaginationInfo pagination = ApiResponse.PaginationInfo.builder()
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.build();

		ApiResponse.ResponseMetadata metadata =
				ApiResponse.ResponseMetadata.builder().pagination(pagination).build();

		return ResponseEntity.ok(ApiResponse.builder()
				.timestamp(now())
				.code(SUCCESS_CODE)
				.message(message)
				.data(page.getContent())
				.metadata(metadata)
				.build());
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, HttpStatus status) {
		return error(errorCode, null, status, null);
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, String detail, HttpStatus status) {
		return error(errorCode, detail, status, null);
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> error(
			ErrorCode errorCode, String detail, HttpStatus status, List<ErrorDetail> errors) {
		OffsetDateTime timestamp = now();
		return ResponseEntity.status(status)
				.body(ApiResponse.builder()
						.timestamp(timestamp)
						.code(errorCode.getCode())
						.message(errorCode.getMessage())
						.error(ApiResponse.ErrorDetails.builder()
								.code(errorCode.getCode())
								.message(errorCode.getMessage())
								.details(detail)
								.validationErrors(errors != null && !errors.isEmpty() ? errors : null)
								.timestamp(timestamp)
								.build())
						.build());
	}

	private OffsetDateTime now() {
		return OffsetDateTime.now(ZoneOffset.UTC);
	}
}
