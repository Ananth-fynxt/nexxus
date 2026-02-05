package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;

public interface FailureRateValidationService {
	boolean isFailureRateValid(Psp psp, RequestInputDto request);

	List<Psp> filterValidFailureRates(List<Psp> psps, RequestInputDto request);
}
