package fynxt.database.config;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@AutoConfiguration
@ConditionalOnClass(EntityManager.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ComponentScan(basePackages = "fynxt.database")
public class DatabaseAutoConfiguration {}
