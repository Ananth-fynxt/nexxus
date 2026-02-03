package fynxt.brand;

import java.util.Objects;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"fynxt.brand", "fynxt.common"})
@ComponentScan(
		basePackages = {
			"fynxt.brand",
			"fynxt.common",
			"fynxt.webhook",
			"fynxt.email",
			"fynxt.permission",
			"fynxt.database"
		})
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "fynxt.brand",
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager")
public class BrandApplication {

	@Bean
	@Primary
	public DataSource dataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	@Bean(name = "entityManagerFactory")
	@DependsOn("flowSchemaBootstrap")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("dataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {
		return builder.dataSource(dataSource)
				.packages(
						"fynxt.brand", "fynxt.flowtype", "fynxt.flowaction", "fynxt.flowtarget", "fynxt.flowdefinition")
				.build();
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
	}

	public static void main(String[] args) {
		SpringApplication.run(BrandApplication.class, args);
	}
}
