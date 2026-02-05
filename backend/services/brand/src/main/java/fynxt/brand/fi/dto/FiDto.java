package fynxt.brand.fi.dto;

import fynxt.brand.user.enums.UserStatus;
import fynxt.common.enums.Scope;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class FiDto {
	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Short id;

	@NotBlank(message = "FI name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Nexxus Financial Group")
	private String name;

	@NotBlank(message = "FI email is required") @Email(message = "Invalid email format") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "contact@fynxt.brand.com")
	private String email;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer userId;

	@Builder.Default
	@Schema(example = "FI", defaultValue = "FI")
	private Scope scope = Scope.FI;

	@Builder.Default
	@Schema(example = "ACTIVE", defaultValue = "ACTIVE")
	private UserStatus status = UserStatus.ACTIVE;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;
}
