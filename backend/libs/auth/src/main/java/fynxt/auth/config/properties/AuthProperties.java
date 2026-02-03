package fynxt.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fynxt.auth")
public class AuthProperties {

	private String apiPrefix;

	private String frontendUrl;

	private String adminToken;
}
