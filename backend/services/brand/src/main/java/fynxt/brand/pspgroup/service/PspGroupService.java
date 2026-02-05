package fynxt.brand.pspgroup.service;

import fynxt.brand.pspgroup.dto.PspGroupDto;

import java.util.List;
import java.util.UUID;

public interface PspGroupService {

	PspGroupDto create(PspGroupDto pspGroupDto);

	PspGroupDto readLatest(Integer id);

	List<PspGroupDto> readByBrandAndEnvironment(UUID brandId, UUID environmentId);

	List<PspGroupDto> readByPspId(UUID pspId);

	PspGroupDto update(Integer id, PspGroupDto pspGroupDto);

	void delete(Integer id);
}
