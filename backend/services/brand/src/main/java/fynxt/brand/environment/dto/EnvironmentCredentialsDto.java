package fynxt.brand.environment.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentCredentialsDto {

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID id;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID secret;

	@Schema(example = "550e8400-e29b-41d4-a716-446655440001")
	private UUID token;
}
