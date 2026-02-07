package fynxt.brand.branduser.dto;

import fynxt.brand.user.enums.UserStatus;
import fynxt.common.enums.Scope;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandUserDto {
	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotNull(message = "Brand Role ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
	private Integer brandRoleId;

	@NotBlank(message = "Name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Jane Smith")
	private String name;

	@NotBlank(message = "Email is required") @Email(message = "Email should be valid") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "jane.smith@fynxt.brand.com")
	private String email;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer userId;

	@Builder.Default
	@Schema(example = "BRAND", defaultValue = "BRAND")
	private Scope scope = Scope.BRAND;

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
