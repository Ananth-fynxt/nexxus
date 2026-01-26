package fynxt.permission.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "permission")
public class PermissionProperties {

	private boolean enabled = true;
}
