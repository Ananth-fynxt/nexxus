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
public class RequestInputDto {

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID brandId;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
	private UUID environmentId;

	@NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
	private BigDecimal amount;

	@NotBlank(message = "Currency is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "USD")
	private String currency;

	@NotBlank(message = "Action ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "flow_action_001")
	private String actionId;

	@NotBlank(message = "Country is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "US")
	private String country;

	@NotBlank(message = "Customer ID is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "brand_customer_001")
	private String customerId;

	@NotBlank(message = "Customer Tag is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "premium")
	private String customerTag;

	@NotBlank(message = "Customer Account Type is required") @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "INDIVIDUAL")
	private String customerAccountType;

	@Schema(example = "192.168.1.1", accessMode = Schema.AccessMode.READ_ONLY)
	private String clientIpAddress;
}
