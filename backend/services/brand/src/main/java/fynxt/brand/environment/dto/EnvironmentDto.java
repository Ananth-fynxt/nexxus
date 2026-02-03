package fynxt.brand.environment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvironmentDto {
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Environment name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Production Environment")
	private String name;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID secret;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440001", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID token;

	@Schema(example = "https://app.nexxus.com")
	private String origin;

	@Schema(example = "https://app.nexxus.com/success")
	private String successRedirectUrl;

	@Schema(example = "https://app.nexxus.com/failure")
	private String failureRedirectUrl;

	@NotNull(message = "Brand ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;
}
