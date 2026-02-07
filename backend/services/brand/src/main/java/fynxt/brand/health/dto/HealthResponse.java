package fynxt.brand.health.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

	private String status;

	@JsonProperty("timestamp")
	private String timestamp;

	private String service;

	public static HealthResponse healthy() {
		return HealthResponse.builder()
				.status("healthy")
				.timestamp(Instant.now().toString())
				.service("Backend Service - Nexxus")
				.build();
	}
}
