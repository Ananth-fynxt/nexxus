package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.PspOperation;

import java.util.List;
import java.util.UUID;

public interface PspOperationsService {
	long getCountByPspIdsAndFlowActionIdAndCurrency(List<UUID> pspIds, String flowActionId, String currency);

	boolean validateByPspIdsAndFlowActionIdAndCurrency(List<UUID> pspIds, String flowActionId, String currency);

	PspOperation getPspOperationIfEnabled(UUID pspId, String flowActionId);

	String fetchFlowDefinitionId(UUID pspId, String flowActionId);
}
