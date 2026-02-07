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
public class AuthResponse {

	@Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
	private String accessToken;

	@Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
	private String refreshToken;

	@Schema(example = "Bearer", defaultValue = "Bearer")
	private String tokenType;

	@Schema(example = "2025-11-12T14:03:09.185538+05:30")
	private OffsetDateTime issuedAt;

	@Schema(example = "2025-11-12T15:03:09.185487+05:30")
	private OffsetDateTime expiresAt;

	@Schema(
			example =
					"{\"fi_name\":\"Nexxus Financial Group\",\"auth_type\":\"APPLICATION_USER\",\"user_id\":\"usr_fi_001\"}")
	private Map<String, Object> claims;
}
