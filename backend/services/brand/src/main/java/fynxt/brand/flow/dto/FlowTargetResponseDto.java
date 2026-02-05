package fynxt.brand.flow.dto;

import fynxt.flowtarget.dto.FlowTargetDto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowTargetResponseDto {

	@JsonUnwrapped
	private FlowTargetDto flowTarget;

	private List<String> currencies;

	private List<String> countries;

	private List<String> paymentMethods;
}
