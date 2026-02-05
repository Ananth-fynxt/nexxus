package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;

public interface AccessValidationService {
	boolean isAccessValid(Psp psp, RequestInputDto request);

	List<Psp> filterValidAccess(List<Psp> psps, RequestInputDto request);
}
