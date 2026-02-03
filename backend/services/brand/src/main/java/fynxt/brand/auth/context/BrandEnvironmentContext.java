package fynxt.brand.auth.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BrandEnvironmentContext {

	private UUID brandId;
	private UUID environmentId;
	private Integer roleId;
	private Integer userId;
	private String scope;
	private String authType;
	private Short fiId;
	private String customerId;
	private List<UUID> accessibleBrandIds;
	private Map<String, Object> rolePermissions;
}
