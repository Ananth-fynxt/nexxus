package fynxt.auth.config;

import fynxt.auth.config.properties.AuthProperties;
import fynxt.auth.config.properties.JwtProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableConfigurationProperties({AuthProperties.class, JwtProperties.class, RouteConfig.class})
@ComponentScan(basePackages = {"fynxt.auth.strategy", "fynxt.auth.filter", "fynxt.auth.config"})
public class AuthAutoConfiguration {}
