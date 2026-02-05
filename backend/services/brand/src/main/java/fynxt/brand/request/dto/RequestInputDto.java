package fynxt.brand.request.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request input data transfer object for PSP fetching")
public class RequestInputDto {

	@Schema(
			description = "Unique identifier of the brand (extracted from authentication context, read-only)",
			example = "550e8400-e29b-41d4-a716-446655440000",
			accessMode = Schema.AccessMode.READ_ONLY)
	private UUID brandId;

	@Schema(
			description = "Unique identifier of the environment (extracted from authentication context, read-only)",
			example = "550e8400-e29b-41d4-a716-446655440000",
			accessMode = Schema.AccessMode.READ_ONLY)
	private UUID environmentId;

	@NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") @Schema(description = "Transaction amount", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
	private BigDecimal amount;

	@NotBlank(message = "Currency is required") @Schema(
			description = "Currency code (ISO 4217 format)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "USD")
	private String currency;

	@NotBlank(message = "Action ID is required") @Schema(
			description = "Unique identifier of the flow action",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "flow_action_001")
	private String actionId;

	@NotBlank(message = "Country is required") @Schema(
			description = "Country code (ISO 3166-1 alpha-2)",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "US")
	private String country;

	@NotBlank(message = "Customer ID is required") @Schema(
			description = "Unique identifier of the customer",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "brand_customer_001")
	private String customerId;

	@NotBlank(message = "Customer Tag is required") @Schema(
			description = "Customer tag for categorization",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "premium")
	private String customerTag;

	@NotBlank(message = "Customer Account Type is required") @Schema(
			description = "Type of customer account",
			requiredMode = Schema.RequiredMode.REQUIRED,
			example = "INDIVIDUAL")
	private String customerAccountType;

	@Schema(
			description = "Client IP address (auto-extracted from request, read-only)",
			example = "192.168.1.1",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String clientIpAddress;
}
