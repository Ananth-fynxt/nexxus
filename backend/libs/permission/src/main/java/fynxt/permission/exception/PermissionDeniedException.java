package fynxt.permission.exception;

import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.BaseException;
import fynxt.common.exception.ErrorCategory;

public class PermissionDeniedException extends BaseException {

	public PermissionDeniedException(String message) {
		super(message, ErrorCode.AUTH_INSUFFICIENT_PERMISSIONS, ErrorCategory.FORBIDDEN);
	}

	public PermissionDeniedException(String message, String detail) {
		super(
				message + (detail != null && !detail.isBlank() ? ": " + detail : ""),
				ErrorCode.AUTH_INSUFFICIENT_PERMISSIONS,
				ErrorCategory.FORBIDDEN);
	}
}
