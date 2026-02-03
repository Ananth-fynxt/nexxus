package fynxt.webhook.config;

import fynxt.webhook.executor.DefaultWebhookExecutor;
import fynxt.webhook.executor.WebhookExecutor;
import fynxt.webhook.retry.ExponentialBackoffRetryStrategy;
import fynxt.webhook.retry.RetryStrategy;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnProperty(name = "webhook.enabled", havingValue = "true")
@EnableConfigurationProperties(WebhookProperties.class)
public class WebhookAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "webhookRestTemplate")
	public RestTemplate webhookRestTemplate(WebhookProperties properties) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(properties.connectTimeoutMs());
		factory.setReadTimeout(properties.readTimeoutMs());
		return new RestTemplate(factory);
	}

	@Bean
	@ConditionalOnMissingBean
	public WebhookExecutor webhookExecutor(RestTemplate webhookRestTemplate) {
		return new DefaultWebhookExecutor(webhookRestTemplate);
	}

	@Bean
	@ConditionalOnMissingBean
	public RetryStrategy retryStrategy(WebhookProperties properties) {
		return new ExponentialBackoffRetryStrategy(properties);
	}
}
