package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;

public interface IpValidationService {
	boolean isIpValid(Psp psp, RequestInputDto request);

	List<Psp> filterValidIps(List<Psp> psps, RequestInputDto request);
}
