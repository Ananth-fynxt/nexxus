package fynxt.brand.transactionlimit.dto;

import fynxt.common.enums.Status;
import fynxt.shared.dto.IdNameDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction limit data transfer object")
public class TransactionLimitDto {
	@Schema(
			description = "Unique identifier of the transaction limit (auto-generated, read-only)",
			example = "txn_limit_001",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(
			description = "Version number of the transaction limit configuration",
			example = "1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer version;

	@NotBlank(message = "Transaction limit name is required") @Schema(
			description = "Name of the transaction limit configuration",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "High Value Transaction Limit")
	private String name;

	@Schema(
			description = "Unique identifier of the brand associated with this transaction limit",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment associated with this transaction limit",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID environmentId;

	@NotBlank(message = "Currency is required") @Schema(
			description = "Currency code (ISO 4217 format)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "USD")
	private String currency;

	@NotEmpty(message = "At least one country is required") @Schema(
			description = "List of country codes where this transaction limit applies (ISO 3166-1 alpha-2)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "[\"US\", \"GB\", \"CA\"]")
	private List<String> countries;

	@NotEmpty(message = "At least one customer tag is required") @Schema(
			description = "List of customer tags that this transaction limit applies to",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "[\"premium\", \"vip\"]")
	private List<String> customerTags;

	@Builder.Default
	@Schema(
			description = "Current status of the transaction limit (ENABLED or DISABLED)",
			example = "ENABLED",
			defaultValue = "ENABLED")
	private Status status = Status.ENABLED;

	@NotEmpty(message = "At least one PSP action is required") @Valid @Schema(description = "List of PSP actions with their limits", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<TransactionLimitPspActionDto> pspActions;

	@NotEmpty(message = "At least one PSP is required") @Schema(
			description = "List of Payment Service Providers (PSPs) associated with this transaction limit",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<IdNameDto> psps;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the transaction limit was created",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(
			description = "Timestamp when the transaction limit was last updated",
			example = "2024-01-15T10:30:00",
			accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(
			description = "User ID who created the transaction limit",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer createdBy;

	@Schema(
			description = "User ID who last updated the transaction limit",
			example = "user_789",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Integer updatedBy;
}
