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
public class PspGroupDto {
	@Schema(example = "psp_group_001", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "PSP group name is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Primary Payment Processors")
	private String name;

	@NotBlank(message = "Flow Action ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "flow_action_001")
	private String flowActionId;

	@Schema(example = "Process Payment", accessMode = Schema.AccessMode.READ_ONLY)
	private String flowActionName;

	@NotBlank(message = "Currency is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "USD")
	private String currency;

	@Builder.Default
	@Schema(example = "ENABLED", defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one PSP is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<IdNameDto> psps;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(example = "user_789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(example = "user_789", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
