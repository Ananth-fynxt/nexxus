package fynxt.brand.config;

import fynxt.brand.config.properties.RouteProperties;
import fynxt.brand.config.properties.SwaggerProperties;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SwaggerProperties.class, RouteProperties.class})
public class SwaggerConfig {

	private final SwaggerProperties swaggerProperties;
	private final RouteProperties routeProperties;

	public SwaggerConfig(SwaggerProperties swaggerProperties, RouteProperties routeProperties) {
		this.swaggerProperties = swaggerProperties;
		this.routeProperties = routeProperties;
	}

	@Bean
	public OpenAPI customOpenAPI() {
		if (!swaggerProperties.enabled()) {
			return new OpenAPI().info(new Info().description("API documentation is currently disabled"));
		}

		OpenAPI openAPI = new OpenAPI().components(createComponents()).addSecurityItem(createSecurityRequirement());

		Info info = createInfo();
		if (info != null) {
			openAPI.info(info);
		}

		List<Server> servers = createServers();
		if (!servers.isEmpty()) {
			openAPI.servers(servers);
		}

		return openAPI;
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public")
				.pathsToMatch("/nexxus/v1/**")
				.packagesToScan("fynxt")
				.addOpenApiCustomizer(globalHeadersCustomiser())
				.build();
	}

	private Info createInfo() {
		String title = swaggerProperties.title();
		if (title == null || title.isBlank()) {
			return null;
		}
		return new Info().title(title);
	}

	private List<Server> createServers() {
		String urls = swaggerProperties.serverUrls();
		if (urls == null || urls.isBlank()) {
			return List.of();
		}
		return List.of(urls.split(",")).stream()
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.map(url -> new Server().url(url))
				.toList();
	}

	private Components createComponents() {
		return new Components().addSecuritySchemes("bearerAuth", createBearerAuth());
	}

	private SecurityScheme createBearerAuth() {
		return new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description(
						"JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");
	}

	private SecurityRequirement createSecurityRequirement() {
		return new SecurityRequirement().addList("bearerAuth");
	}

	@Bean
	public OpenApiCustomizer globalHeadersCustomiser() {
		Set<String> brandEnvPaths = routeProperties.getBrandEnvPaths().stream()
				.map(path -> path.replace("/**", ""))
				.collect(Collectors.toSet());

		return openApi -> openApi.getPaths().entrySet().forEach(pathEntry -> {
			String path = pathEntry.getKey();
			boolean requiresBrandEnvHeaders =
					brandEnvPaths.stream().anyMatch(brandEnvPath -> path.startsWith(brandEnvPath));

			if (requiresBrandEnvHeaders) {
				pathEntry.getValue().readOperations().forEach(operation -> {
					boolean hasBrandId = operation.getParameters() != null
							&& operation.getParameters().stream().anyMatch(p -> "X-BRAND-ID".equals(p.getName()));
					boolean hasEnvId = operation.getParameters() != null
							&& operation.getParameters().stream().anyMatch(p -> "X-ENV-ID".equals(p.getName()));

					if (!hasBrandId) {
						operation.addParametersItem(createBrandIdHeader());
					} else {
						operation.getParameters().stream()
								.filter(p -> "X-BRAND-ID".equals(p.getName()))
								.findFirst()
								.ifPresent(p -> p.setRequired(true));
					}

					if (!hasEnvId) {
						operation.addParametersItem(createEnvironmentIdHeader());
					} else {
						operation.getParameters().stream()
								.filter(p -> "X-ENV-ID".equals(p.getName()))
								.findFirst()
								.ifPresent(p -> p.setRequired(true));
					}
				});
			}
		});
	}

	private Parameter createBrandIdHeader() {
		return new Parameter()
				.name("X-BRAND-ID")
				.example("brn_001")
				.in("header")
				.description("Brand ID for authentication.")
				.required(true)
				.schema(new StringSchema());
	}

	private Parameter createEnvironmentIdHeader() {
		return new Parameter()
				.name("X-ENV-ID")
				.example("env_uat_001")
				.in("header")
				.description("Environment ID for authentication.")
				.required(true)
				.schema(new StringSchema());
	}
}
