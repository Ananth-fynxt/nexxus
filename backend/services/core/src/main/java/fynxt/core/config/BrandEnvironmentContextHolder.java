package fynxt.core.config;

import fynxt.permission.context.PermissionContextHolder;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BrandEnvironmentContextHolder implements PermissionContextHolder {

	@Override
	public String getScope() {
		return "EXTERNAL";
	}

	@Override
	public Map<String, Object> getRolePermissions() {
		return Collections.emptyMap();
	}
}
