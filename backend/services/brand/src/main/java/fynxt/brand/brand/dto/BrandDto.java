package fynxt.brand.brand.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandDto {
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Brand name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Nexxus Payment Gateway")
	private String name;

	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
	private Short fiId;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Email(message = "Invalid email Id") @Schema(example = "contact@fynxt.brand.com")
	private String email;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private List<EnvironmentInfo> environments;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema
	public static class EnvironmentInfo {
		@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
		private UUID id;

		@Schema(example = "sec_3zAW1sFgUm87TXVzEZb0UrhM0O", accessMode = Schema.AccessMode.READ_ONLY)
		private UUID apiKey;

		@Schema(example = "Production", accessMode = Schema.AccessMode.READ_ONLY)
		private String name;
	}
}
