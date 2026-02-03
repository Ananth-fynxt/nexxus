package fynxt.brand.brandrole.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class BrandRoleDto {
	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@NotNull(message = "Brand ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@NotNull(message = "Environment ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "Role name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Admin")
	private String name;

	@NotBlank(message = "Permission is required") @Schema(
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "{\"modules\": [{\"name\": \"transactions\", \"actions\": [\"read\", \"create\"]}]}")
	private String permission;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;
}
