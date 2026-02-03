package fynxt.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request with email and password")
public class LoginRequest {

	@NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Schema(
			description = "User email address",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "admin@nexxus.com")
	private String email;

	@NotBlank(message = "Password is required") @Schema(description = "User password", requiredMode = Schema.RequiredMode.REQUIRED, example = "your-password")
	private String password;
}
