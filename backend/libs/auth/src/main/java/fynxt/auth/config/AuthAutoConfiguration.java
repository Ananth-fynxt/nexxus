package fynxt.auth.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = {"fynxt.auth.strategy", "fynxt.auth.filter", "fynxt.auth.config"})
public class AuthAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RouteConfig routeConfig() {
		return new RouteConfig();
	}
}
