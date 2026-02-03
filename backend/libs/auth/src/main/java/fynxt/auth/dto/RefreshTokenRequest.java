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
@Schema(description = "Refresh token request")
public class RefreshTokenRequest {

	@NotBlank(message = "Refresh token is required") @Schema(
			description = "JWT refresh token",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "eyJhbGciOiJIUzI1NiJ9...")
	private String refreshToken;
}
