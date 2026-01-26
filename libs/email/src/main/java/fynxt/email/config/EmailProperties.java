package fynxt.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

	private boolean enabled = false;
	private String senderAddress;
	private String connectionString;
	private int threadPoolSize = 5;
}
