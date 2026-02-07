package fynxt.brand.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "api")
public record ApiProperties(
		@NotBlank String prefix,
		@NotBlank String backendServerUrl,
		@NotBlank String frontendUrl,
		@NotBlank String widgetUrl) {}
