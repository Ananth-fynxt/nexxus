package fynxt.auth.config;

import fynxt.auth.config.properties.AuthProperties;
import fynxt.auth.filter.AccessTokenOncePerRequestFilter;
import fynxt.auth.filter.AuthenticationStrategy;
import fynxt.auth.filter.CorrelationIdWebFilter;
import fynxt.auth.filter.RawBodyCachingFilter;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AuthProperties authProperties;
	private final RouteConfig routeConfig;
	private final List<AuthenticationStrategy> authenticationStrategies;

	public SecurityConfig(
			AuthProperties authProperties,
			RouteConfig routeConfig,
			List<AuthenticationStrategy> authenticationStrategies) {
		this.authProperties = authProperties;
		this.routeConfig = routeConfig;
		this.authenticationStrategies = authenticationStrategies;
	}

	@Bean
	public AccessTokenOncePerRequestFilter accessTokenOncePerRequestFilter() {
		return new AccessTokenOncePerRequestFilter(routeConfig, authenticationStrategies);
	}

	@Bean
	public SecurityFilterChain customSecurityFilterChain(
			HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource,
			AccessTokenOncePerRequestFilter accessTokenOncePerRequestFilter)
			throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.authorizeHttpRequests(authz -> authz.requestMatchers(
								"/dashboard-action/**",
								"/fynxt-flow-dashboard/**",
								"/favicon.ico",
								"/error",
								"/actuator/**")
						.permitAll()
						.requestMatchers(routeConfig.getPublicPaths())
						.permitAll()
						.requestMatchers(routeConfig.getAdminTokenPaths())
						.permitAll()
						.requestMatchers(routeConfig.getSecretTokenPaths())
						.permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
						.permitAll()
						.anyRequest()
						.authenticated())
				.addFilterAfter(new RawBodyCachingFilter(), SecurityContextHolderFilter.class)
				.addFilterAfter(new CorrelationIdWebFilter(), RawBodyCachingFilter.class)
				.addFilterAfter(accessTokenOncePerRequestFilter, CorrelationIdWebFilter.class);
		return http.build();
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(
			@Qualifier("corsConfigurationSource") CorsConfigurationSource source) {
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// CORS configuration for SDK/Widget endpoints (open for browser access)
		CorsConfiguration sdkConfiguration = new CorsConfiguration();
		sdkConfiguration.addAllowedOriginPattern("*"); // Allow all origins for SDK
		sdkConfiguration.setAllowedMethods(Arrays.asList(getAllowedCorsMethods()));
		sdkConfiguration.addAllowedHeader("*");
		sdkConfiguration.setAllowCredentials(false); // No credentials needed for SDK

		// CORS configuration for secure internal APIs (restricted)
		CorsConfiguration secureConfiguration = new CorsConfiguration();
		String frontendUrl = authProperties.frontendUrl();
		List<String> allowedOrigins =
				frontendUrl == null || frontendUrl.isBlank() ? List.of() : Arrays.asList(frontendUrl.split(","));
		secureConfiguration.setAllowedOriginPatterns(allowedOrigins);
		secureConfiguration.setAllowedMethods(Arrays.asList(getAllowedCorsMethods()));
		secureConfiguration.addAllowedHeader("*");
		secureConfiguration.setAllowCredentials(true);

		// Apply SDK CORS to open-for-all-origins endpoints
		String[] openForAllOriginsPaths = routeConfig.getOpenForAllOriginsPaths();
		for (String path : openForAllOriginsPaths) {
			source.registerCorsConfiguration(path, sdkConfiguration);
		}

		// Apply secure CORS to all other endpoints
		source.registerCorsConfiguration("/**", secureConfiguration);

		return source;
	}

	private String[] getAllowedCorsMethods() {
		return new String[] {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configurePathMatch(PathMatchConfigurer configurer) {
				String apiPrefix = authProperties != null ? authProperties.apiPrefix() : null;
				if (StringUtils.isNotBlank(apiPrefix)) {
					configurer.addPathPrefix(
							apiPrefix,
							c -> c.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class));
				}
			}
		};
	}
}
