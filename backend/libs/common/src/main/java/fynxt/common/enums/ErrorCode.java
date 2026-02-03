package fynxt.common.enums;

public enum ErrorCode {
	SUCCESS("0000", "Success"),
	GENERIC_ERROR("1000", "An unexpected error occurred"),
	VALIDATION_ERROR("1001", "Validation failed"),
	INVALID_REQUEST("1002", "Invalid request format"),
	RESOURCE_NOT_FOUND("1003", "Requested resource not found"),
	UNAUTHORIZED("1004", "Unauthorized access"),
	FORBIDDEN("1005", "Access forbidden"),
	CONFLICT("1006", "Resource conflict"),
	DUPLICATE_RESOURCE("1007", "Resource already exists"),
	INVALID_CREDENTIALS("1008", "Invalid credentials"),
	UNEXPECTED_ERROR("1009", "An unexpected error occurred. Please try again later."),

	AUTH_INVALID_CREDENTIALS("1100", "Invalid credentials"),
	AUTH_INSUFFICIENT_PERMISSIONS("1101", "Insufficient permissions"),
	AUTHENTICATION_REQUIRED("1102", "Authentication required"),
	INVALID_TOKEN("1103", "Invalid token"),
	SECRET_TOKEN_INVALID("1104", "Invalid secret token"),
	AUTHENTICATION_FAILED("1105", "Authentication failed"),

	BRAND_NOT_FOUND("1200", "Brand not found"),
	BRAND_ALREADY_EXISTS("1201", "Brand already exists"),
	BRAND_ROLE_NOT_FOUND("1202", "Brand role not found"),
	BRAND_ROLE_ALREADY_EXISTS("1203", "Brand role already exists"),
	BRAND_USER_NOT_FOUND("1204", "Brand user not found"),
	BRAND_USER_ALREADY_EXISTS("1205", "Brand user already exists"),

	ENVIRONMENT_NOT_FOUND("1300", "Environment not found"),
	ENVIRONMENT_ALREADY_EXISTS("1301", "Environment already exists"),

	FI_NOT_FOUND("1350", "Financial Institution not found"),
	FI_ALREADY_EXISTS("1351", "Financial Institution already exists"),
	FI_EMAIL_ALREADY_EXISTS("1352", "Financial Institution email already exists"),

	PSP_NOT_FOUND("1400", "Payment service provider not found"),
	PSP_STATUS_INVALID("1401", "PSP status is invalid"),
	PSP_OPERATION_NOT_FOUND("1402", "PSP operation not found"),
	PSP_OPERATION_STATUS_INVALID("1403", "PSP operation status is invalid"),

	USER_NOT_FOUND("1500", "User not found"),
	USER_ALREADY_EXISTS("1501", "User already exists"),
	USER_PASSWORD_TOO_WEAK("1502", "User password is too weak"),
	USER_NO_ACCESS("1503", "User has no access permissions"),

	TOKEN_ISSUANCE_FAILED("1600", "Failed to issue token"),
	TOKEN_VALIDATION_FAILED("1601", "Token validation failed"),
	TOKEN_SECRET_GENERATION_FAILED("1602", "Failed to generate token secret"),

	TRANSACTION_NOT_FOUND("1700", "Transaction not found"),
	TRANSACTION_VALIDATION_FAILED("1701", "Transaction validation failed"),
	TRANSACTION_PROCESSING_ERROR("1702", "Transaction processing error"),
	TRANSACTION_NO_VALID_STEPS_FOUND("1703", "No valid transaction steps found"),
	TRANSACTION_MULTIPLE_STEPS_FOUND("1704", "Multiple valid transaction steps found"),
	TRANSACTION_INVALID_STATUS("1705", "Invalid transaction status"),
	TRANSACTION_INVALID_TRANSITION_STATUS("1706", "Invalid transaction transition status"),
	TRANSACTION_REQUEST_ID_NOT_FOUND("1707", "Request Id is required"),
	TRANSACTION_DUPLICATE("1708", "Duplicate transaction detected");

	private final String code;
	private final String message;

	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return code;
	}
}
