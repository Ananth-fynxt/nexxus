package fynxt.permission.aspects;

import fynxt.permission.annotations.RequiresPermission;
import fynxt.permission.exception.PermissionDeniedException;
import fynxt.permission.services.PermissionService;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class PermissionCheckAspect {

	private final PermissionService permissionService;

	@Around("@annotation(requiresPermission)")
	public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission)
			throws Throwable {

		String module = requiresPermission.module();
		String action = requiresPermission.action();

		boolean hasPermission = permissionService.hasPermission(module, action);

		if (!hasPermission) {
			String errorMessage = requiresPermission.errorMessage();
			if (errorMessage != null && !errorMessage.isEmpty()) {
				throw new PermissionDeniedException(errorMessage);
			}
			throw new PermissionDeniedException(
					String.format("You don't have permission to '%s' on '%s' module", action, module));
		}

		return joinPoint.proceed();
	}
}
