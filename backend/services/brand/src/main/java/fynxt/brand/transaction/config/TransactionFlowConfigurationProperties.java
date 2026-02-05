package fynxt.brand.transaction.config;

import java.time.Duration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "transaction.flow-configuration.cache")
public class TransactionFlowConfigurationProperties {

	private int maximumSize = 5000;
	private Duration expireAfterWrite = Duration.ofMinutes(5);
}
