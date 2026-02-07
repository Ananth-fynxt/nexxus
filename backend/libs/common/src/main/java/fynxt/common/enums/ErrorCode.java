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
	MISSING_REQUIRED_PARAMETER("1010", "Missing required parameter"),

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
	TRANSACTION_DUPLICATE("1708", "Duplicate transaction detected"),
	TRANSACTION_AMOUNT_REQUIRED("1709", "Amount is required"),
	TRANSACTION_AMOUNT_INVALID("1710", "Invalid transaction amount"),
	TRANSACTION_FLOW_ACTION_ID_REQUIRED("1711", "Flow action ID is required"),
	TRANSACTION_CURRENCY_REQUIRED("1712", "Currency is required"),
	TRANSACTION_CURRENCY_INVALID("1713", "Invalid transaction currency"),
	FLOW_DEFINITION_NOT_FOUND("1714", "Flow definition not found"),
	FLOW_DEFINITION_CODE_REQUIRED("1715", "Flow definition code is required"),
	INVALID_REQUEST_BODY("1716", "Invalid request body"),

	SESSION_NOT_FOUND("1720", "Session not found"),
	SESSION_EXPIRED("1721", "Session expired"),
	SESSION_INVALID_TOKEN("1722", "Invalid session token"),

	TOKEN_DECRYPTION_FAILED("1603", "Token decryption failed"),

	TRANSACTION_LIMIT_NOT_FOUND("1730", "Transaction limit not found"),
	TRANSACTION_LIMIT_ALREADY_EXISTS("1731", "Transaction limit already exists"),

	ROUTING_RULE_NOT_FOUND("1740", "Routing rule not found"),

	RISK_RULE_NOT_FOUND("1750", "Risk rule not found"),
	PSP_CONFIGURATION_ERROR("1751", "PSP configuration error"),
	RISK_RULE_CRITERIA_TYPE_REQUIRED("1752", "Risk rule criteria type is required"),
	RISK_RULE_CRITERIA_VALUE_REQUIRED("1753", "Risk rule criteria value is required"),

	PSP_GROUP_NOT_FOUND("1404", "PSP group not found"),
	PSP_GROUP_ALREADY_EXISTS("1405", "PSP group already exists"),

	FEE_NOT_FOUND("1450", "Fee not found");

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

	public static ErrorCode fromCode(String code) {
		if (code == null || code.isBlank()) {
			return null;
		}
		for (ErrorCode errorCode : values()) {
			if (errorCode.code.equals(code)) {
				return errorCode;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return code;
	}
}
