package fynxt.brand.auth.context;

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
}
