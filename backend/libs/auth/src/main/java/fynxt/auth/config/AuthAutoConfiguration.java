package fynxt.auth.config;

import fynxt.auth.config.properties.AuthProperties;
import fynxt.auth.config.properties.JwtProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableConfigurationProperties({AuthProperties.class, JwtProperties.class})
@ComponentScan(basePackages = {"fynxt.auth.strategy", "fynxt.auth.filter", "fynxt.auth.config"})
public class AuthAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RouteConfig routeConfig() {
		return new RouteConfig();
	}
}
