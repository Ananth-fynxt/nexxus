package fynxt.auth.util;

import fynxt.common.constants.ErrorCode;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ErrorResponseUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.registerModule(new JavaTimeModule());
	}

	private ErrorResponseUtil() {}

	public static void writeErrorResponse(
			HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode, HttpStatus httpStatus)
			throws IOException {

		response.setStatus(httpStatus.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("error", createErrorDetails(errorCode, request));
		errorResponse.put("timestamp", OffsetDateTime.now());
		errorResponse.put("path", request.getRequestURI());

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		response.getWriter().flush();
	}

	private static Map<String, Object> createErrorDetails(ErrorCode errorCode, HttpServletRequest request) {
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("code", errorCode.getCode());
		errorDetails.put("message", errorCode.getMessage());
		errorDetails.put("category", "AUTHENTICATION");

		String correlationId = request.getHeader("X-Correlation-ID");
		if (correlationId != null) {
			errorDetails.put("correlationId", correlationId);
		}

		return errorDetails;
	}
}
