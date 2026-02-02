package fynxt.brand.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "swagger")
public record SwaggerProperties(boolean enabled, String title, String serverUrls) {}
