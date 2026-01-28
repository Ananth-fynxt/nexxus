package fynxt.permission.config;

import fynxt.permission.aspects.PermissionCheckAspect;
import fynxt.permission.aspects.ScopeCheckAspect;
import fynxt.permission.context.PermissionContextHolder;
import fynxt.permission.services.PermissionService;
import fynxt.permission.services.impl.PermissionServiceImpl;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "permission.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PermissionProperties.class)
public class PermissionAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PermissionService permissionService(PermissionContextHolder contextHolder) {
		return new PermissionServiceImpl(contextHolder);
	}

	@Bean
	@ConditionalOnMissingBean
	public PermissionCheckAspect permissionCheckAspect(PermissionService permissionService) {
		return new PermissionCheckAspect(permissionService);
	}

	@Bean
	@ConditionalOnMissingBean
	public ScopeCheckAspect scopeCheckAspect(PermissionContextHolder contextHolder) {
		return new ScopeCheckAspect(contextHolder);
	}
}
