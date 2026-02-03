package fynxt.brand.config;

import fynxt.brand.auth.context.BrandEnvironmentContext;
import fynxt.permission.context.PermissionContextHolder;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BrandEnvironmentContextHolder implements PermissionContextHolder {

	@Override
	public String getScope() {
		BrandEnvironmentContext context = fynxt.brand.auth.context.BrandEnvironmentContextHolder.getContext();
		return context != null ? context.getScope() : null;
	}

	@Override
	public Map<String, Object> getRolePermissions() {
		BrandEnvironmentContext context = fynxt.brand.auth.context.BrandEnvironmentContextHolder.getContext();
		return context != null ? context.getRolePermissions() : null;
	}
}
