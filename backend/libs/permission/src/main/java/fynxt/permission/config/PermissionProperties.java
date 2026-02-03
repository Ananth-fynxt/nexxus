package fynxt.permission.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "permission")
public record PermissionProperties(Boolean enabled) {}
