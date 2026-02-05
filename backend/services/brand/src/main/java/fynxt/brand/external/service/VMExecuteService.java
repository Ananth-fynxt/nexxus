package fynxt.brand.external.service;

import fynxt.brand.external.dto.VmExecutionDto;
import fynxt.denovm.dto.DenoVMResult;

public interface VMExecuteService {
	DenoVMResult executeVmRequest(VmExecutionDto requestDto);
}
