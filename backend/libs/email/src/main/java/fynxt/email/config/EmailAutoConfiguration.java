package fynxt.email.config;

import fynxt.email.EmailService;
import fynxt.email.impl.EmailServiceImpl;
import fynxt.email.template.EmailTemplateService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "email.enabled", havingValue = "true")
@EnableConfigurationProperties(EmailProperties.class)
public class EmailAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "email.connection-string")
	public EmailAsyncClient emailAsyncClient(EmailProperties properties) {
		return new EmailClientBuilder()
				.connectionString(properties.connectionString())
				.buildAsyncClient();
	}

	@Bean
	@ConditionalOnMissingBean
	public ExecutorService emailExecutorService(EmailProperties properties) {
		return Executors.newFixedThreadPool(properties.threadPoolSize());
	}

	@Bean
	@ConditionalOnMissingBean
	public EmailService emailService(
			EmailProperties emailProperties,
			EmailTemplateService emailTemplateService,
			ExecutorService emailExecutorService) {
		EmailAsyncClient emailAsyncClient = null;
		try {
			emailAsyncClient = applicationContext.getBean(EmailAsyncClient.class);
		} catch (Exception e) {
			// Email client not configured, will be handled in service
		}
		return new EmailServiceImpl(emailProperties, emailTemplateService, emailAsyncClient, emailExecutorService);
	}
}
