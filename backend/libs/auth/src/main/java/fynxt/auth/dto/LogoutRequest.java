package fynxt.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Logout request")
public class LogoutRequest {

	@NotBlank(message = "Authorization header is required") @Schema(
			description = "Authorization Bearer token",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
	private String authorization;
}
