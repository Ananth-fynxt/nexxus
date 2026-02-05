package fynxt.brand.pspgroup.dto;

import fynxt.common.enums.Status;
import fynxt.shared.dto.IdNameDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "PSP group data transfer object")
public class PspGroupDto {
	@Schema(
			description = "Unique identifier of the PSP group (auto-generated, read-only)",
			example = "psp_group_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(
			description = "Version number of the PSP group configuration",
			example = "1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@Schema(
			description = "Unique identifier of the brand associated with this PSP group",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this PSP group",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "PSP group name is required") @Schema(
			description = "Name of the PSP group",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "Primary Payment Processors")
	private String name;

	@NotBlank(message = "Flow Action ID is required") @Schema(
			description = "Unique identifier of the flow action",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "flow_action_001")
	private String flowActionId;

	@Schema(
			description = "Name of the flow action (read-only, populated from flowActionId)",
			example = "Process Payment",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String flowActionName;

	@NotBlank(message = "Currency is required") @Schema(
			description = "Currency code (ISO 4217 format)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "USD")
	private String currency;

	@Builder.Default
	@Schema(
			description = "Current status of the PSP group (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one PSP is required") @Schema(
			description = "List of Payment Service Providers (PSPs) in this group",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<IdNameDto> psps;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the PSP group was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the PSP group was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the PSP group",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the PSP group",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
