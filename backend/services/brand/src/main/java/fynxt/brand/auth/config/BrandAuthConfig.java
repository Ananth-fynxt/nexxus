package fynxt.brand.auth.config;

import fynxt.auth.service.EnvironmentLookupService;
import fynxt.auth.service.UserAuthenticationService;
import fynxt.brand.auth.service.impl.BrandEnvironmentLookupServiceImpl;
import fynxt.brand.auth.service.impl.BrandUserAuthenticationServiceImpl;
import fynxt.brand.auth.service.impl.TokenValidationServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "fynxt.brand.auth.repository")
@EnableJpaAuditing
public class BrandAuthConfig {

	@Bean
	@ConditionalOnMissingBean
	public UserAuthenticationService userAuthenticationService(BrandUserAuthenticationServiceImpl impl) {
		return impl;
	}

	@Bean
	@ConditionalOnMissingBean
	public EnvironmentLookupService environmentLookupService(BrandEnvironmentLookupServiceImpl impl) {
		return impl;
	}

	@Bean
	@ConditionalOnMissingBean
	public fynxt.auth.service.TokenValidationService tokenValidationService(
			TokenValidationServiceImpl tokenValidationService) {
		return tokenValidationService;
	}
}
