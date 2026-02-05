package fynxt.brand.psp.service.impl;

import fynxt.brand.enums.ErrorCode;
import fynxt.brand.psp.entity.PspOperation;
import fynxt.brand.psp.repository.PspOperationRepository;
import fynxt.brand.psp.service.PspOperationsService;
import fynxt.brand.psp.service.mappers.PspMapper;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PspOperationsServiceImpl implements PspOperationsService {

	private final PspOperationRepository pspOperationRepository;
	private final PspMapper pspMapper;

	public boolean validateByPspIdsAndFlowActionIdAndCurrency(List<UUID> pspIds, String flowActionId, String currency) {
		long recordCount = getCountByPspIdsAndFlowActionIdAndCurrency(pspIds, flowActionId, currency);
		return recordCount == pspIds.size();
	}

	public long getCountByPspIdsAndFlowActionIdAndCurrency(List<UUID> pspIds, String flowActionId, String currency) {
		return pspOperationRepository.countByPspIdsAndFlowActionIdAndCurrency(pspIds, flowActionId, currency);
	}

	@Override
	public PspOperation getPspOperationIfEnabled(UUID pspId, String flowActionId) {
		PspOperation pspOperation = pspOperationRepository.findByPspIdAndFlowActionId(pspId, flowActionId);
		if (pspOperation.getStatus() != Status.ENABLED) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.PSP_OPERATION_STATUS_INVALID.getCode());
		}
		return pspOperation;
	}

	@Override
	public String fetchFlowDefinitionId(UUID pspId, String flowActionId) {
		PspOperation pspOperation = pspOperationRepository.findByPspIdAndFlowActionId(pspId, flowActionId);
		String flowDefinitionId = pspMapper.extractFlowDefinitionId(pspOperation);

		return flowDefinitionId;
	}
}
