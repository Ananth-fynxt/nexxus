package fynxt.permission.services.impl;

import fynxt.permission.context.PermissionContextHolder;
import fynxt.permission.services.PermissionService;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

	private final PermissionContextHolder contextHolder;

	@Override
	public boolean hasPermission(String module, String action) {
		// Check if permission check should be bypassed (for admin/secret token requests)
		if (isPermissionCheckBypassed()) {
			return true;
		}

		String scope = contextHolder.getScope();

		// FI, EXTERNAL scopes don't need permission validation
		if ("FI".equals(scope) || "EXTERNAL".equals(scope)) {
			return true;
		}

		// BRAND scope needs role permission validation
		if ("BRAND".equals(scope)) {
			return validateBrandPermission(module, action);
		}

		return false;
	}

	@Override
	public void requirePermission(String module, String action) {
		if (!hasPermission(module, action)) {
			throw new fynxt.permission.exception.PermissionDeniedException(
					String.format("You don't have permission to '%s' on '%s' module", action, module));
		}
	}

	private boolean validateBrandPermission(String module, String action) {
		Map<String, Object> rolePermissions = contextHolder.getRolePermissions();

		if (rolePermissions == null) {
			return false;
		}

		if (!rolePermissions.containsKey(module)) {
			return false;
		}

		Object moduleObj = rolePermissions.get(module);
		if (!(moduleObj instanceof Map)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> modulePermissions = (Map<String, Object>) moduleObj;

		if (!modulePermissions.containsKey("actions")) {
			return false;
		}

		Object actionsObj = modulePermissions.get("actions");
		if (!(actionsObj instanceof List)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		List<String> actions = (List<String>) actionsObj;

		return actions.contains(action);
	}

	private boolean isPermissionCheckBypassed() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			if (request != null) {
				Object bypassFlag = request.getAttribute("bypass.permission.check");
				return Boolean.TRUE.equals(bypassFlag);
			}
		}
		return false;
	}
}
