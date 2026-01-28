package fynxt.permission.aspects;

import fynxt.permission.annotations.RequiresScope;
import fynxt.permission.context.PermissionContextHolder;
import fynxt.permission.exception.PermissionDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class ScopeCheckAspect {

	private final PermissionContextHolder contextHolder;

	@Around("@within(fynxt.permission.annotations.RequiresScope)")
	public Object checkScope(ProceedingJoinPoint joinPoint) throws Throwable {

		// Check if scope check should be bypassed (for admin/secret token requests)
		if (isPermissionCheckBypassed()) {
			return joinPoint.proceed();
		}

		Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
		RequiresScope annotation = declaringType.getAnnotation(RequiresScope.class);

		if (annotation == null) {
			return joinPoint.proceed();
		}

		String[] allowedScopes = annotation.value();
		String userScope = contextHolder.getScope();

		if (userScope == null) {
			String errorMessage = annotation.errorMessage();
			if (errorMessage != null && !errorMessage.isEmpty()) {
				throw new PermissionDeniedException(errorMessage);
			}
			throw new PermissionDeniedException("Authentication required - no scope found");
		}

		boolean scopeAllowed = false;
		for (String allowedScope : allowedScopes) {
			if (allowedScope.equals(userScope)) {
				scopeAllowed = true;
				break;
			}
		}

		if (!scopeAllowed) {
			String errorMessage = annotation.errorMessage();
			if (errorMessage != null && !errorMessage.isEmpty()) {
				throw new PermissionDeniedException(errorMessage);
			}
			throw new PermissionDeniedException("Access denied - scope '" + userScope + "' not allowed");
		}

		return joinPoint.proceed();
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
