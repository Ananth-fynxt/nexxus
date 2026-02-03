package fynxt.auth.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing access token, refresh token, and token metadata")
public class AuthResponse {

	@Schema(description = "JWT access token for API authentication", example = "eyJhbGciOiJIUzI1NiJ9...")
	private String accessToken;

	@Schema(description = "JWT refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiJ9...")
	private String refreshToken;

	@Schema(description = "Type of token (typically 'Bearer')", example = "Bearer", defaultValue = "Bearer")
	private String tokenType;

	@Schema(description = "Timestamp when the token was issued", example = "2025-11-12T14:03:09.185538+05:30")
	private OffsetDateTime issuedAt;

	@Schema(description = "Timestamp when the access token expires", example = "2025-11-12T15:03:09.185487+05:30")
	private OffsetDateTime expiresAt;

	@Schema(
			description = "JWT claims containing user information, scope, brand/environment details, etc.",
			example =
					"{\"fi_name\":\"Nexxus Financial Group\",\"auth_type\":\"APPLICATION_USER\",\"user_id\":\"usr_fi_001\"}")
	private Map<String, Object> claims;
}
