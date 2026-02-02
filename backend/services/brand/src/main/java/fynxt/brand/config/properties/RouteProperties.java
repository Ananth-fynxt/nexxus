package fynxt.brand.config.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
public record RouteProperties(String prefix, Routes routes) {

	public record Routes(List<String> brandEnvPaths) {}

	public List<String> getBrandEnvPaths() {
		return routes != null && routes.brandEnvPaths() != null ? routes.brandEnvPaths() : List.of();
	}
}
