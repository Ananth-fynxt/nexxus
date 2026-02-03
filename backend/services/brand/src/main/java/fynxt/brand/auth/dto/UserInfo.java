package fynxt.brand.auth.dto;

import fynxt.auth.enums.AuthType;
import fynxt.brand.user.enums.UserStatus;
import fynxt.common.enums.Scope;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {

	private Integer userId;
	private String email;
	private Scope scope;
	private UserStatus status;
	private AuthType authType;

	// For FI scope users
	private Short fiId;
	private String fiName;
	private List<BrandInfo> brands;

	// For BRAND scope users
	private List<BrandInfo> accessibleBrands;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class BrandInfo {
		private UUID id;
		private String name;
		private List<EnvironmentInfo> environments;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class EnvironmentInfo {
		private UUID id;
		private String name;
		private Integer roleId;
	}
}
