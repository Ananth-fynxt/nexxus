package fynxt.common.http;

import fynxt.common.constants.ErrorCode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilderImpl implements ResponseBuilder {

	@Override
	public ResponseEntity<ApiResponse<Object>> success(Object data, String message) {
		return ResponseEntity.ok(ApiResponse.builder()
				.timestamp(now())
				.code("SUCCESS")
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
				.code("SUCCESS")
				.message(message)
				.data(page.getContent())
				.metadata(metadata)
				.build());
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, HttpStatus status) {
		return error(errorCode, null, status);
	}

	@Override
	public ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode, String details, HttpStatus status) {
		return ResponseEntity.status(status)
				.body(ApiResponse.builder()
						.timestamp(now())
						.code(errorCode.getCode())
						.message(errorCode.getMessage())
						.error(ApiResponse.ErrorDetails.builder()
								.code(errorCode.getCode())
								.message(errorCode.getMessage())
								.details(details)
								.build())
						.build());
	}

	private OffsetDateTime now() {
		return OffsetDateTime.now(ZoneOffset.UTC);
	}
}
