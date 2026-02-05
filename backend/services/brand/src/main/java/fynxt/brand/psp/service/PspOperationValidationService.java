package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;

public interface PspOperationValidationService {
	boolean isPspOperationValid(Psp psp, RequestInputDto request);

	List<Psp> filterValidPspOperations(List<Psp> psps, RequestInputDto request);
}
