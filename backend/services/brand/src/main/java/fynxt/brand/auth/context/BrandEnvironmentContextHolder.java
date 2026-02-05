package fynxt.brand.auth.context;

import java.util.Map;
import java.util.UUID;

public class BrandEnvironmentContextHolder {

	private static final ThreadLocal<BrandEnvironmentContext> CONTEXT_HOLDER = new ThreadLocal<>();

	private BrandEnvironmentContextHolder() {}

	public static void setContext(BrandEnvironmentContext context) {
		CONTEXT_HOLDER.set(context);
	}

	public static BrandEnvironmentContext getContext() {
		return CONTEXT_HOLDER.get();
	}

	public static void clearContext() {
		CONTEXT_HOLDER.remove();
	}

	public static UUID getBrandId() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getBrandId() : null;
	}

	public static UUID getEnvironmentId() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getEnvironmentId() : null;
	}

	public static Integer getRoleId() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getRoleId() : null;
	}

	public static Integer getUserId() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getUserId() : null;
	}

	public static String getScope() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getScope() : null;
	}

	public static String getAuthType() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getAuthType() : null;
	}

	public static Map<String, Object> getRolePermissions() {
		BrandEnvironmentContext context = getContext();
		return context != null ? context.getRolePermissions() : null;
	}
}
