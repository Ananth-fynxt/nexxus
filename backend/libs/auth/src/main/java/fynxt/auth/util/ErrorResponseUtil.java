package fynxt.auth.util;

import fynxt.common.enums.ErrorCode;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ErrorResponseUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	private ErrorResponseUtil() {}

	private static String currentUtcTimestamp() {
		return OffsetDateTime.now(ZoneOffset.UTC)
				.truncatedTo(ChronoUnit.SECONDS)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	private static void writeJsonResponse(HttpServletResponse response, Map<String, Object> body, HttpStatus httpStatus)
			throws IOException {
		response.setStatus(httpStatus.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(body));
		response.getWriter().flush();
	}

	public static void writeErrorResponse(
			HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode, HttpStatus httpStatus)
			throws IOException {
		writeErrorResponse(response, errorCode, httpStatus);
	}

	public static void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, HttpStatus httpStatus)
			throws IOException {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("code", errorCode.getCode());
		errorResponse.put("message", errorCode.getMessage());
		errorResponse.put("timestamp", currentUtcTimestamp());
		writeJsonResponse(response, errorResponse, httpStatus);
	}

	public static void writeErrorResponse(HttpServletResponse response, String message, HttpStatus httpStatus)
			throws IOException {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("message", message);
		errorResponse.put("timestamp", currentUtcTimestamp());
		writeJsonResponse(response, errorResponse, httpStatus);
	}
}
