package fynxt.brand.psp.service.impl;

import fynxt.brand.psp.entity.MaintenanceWindow;
import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.repository.MaintenanceWindowRepository;
import fynxt.brand.psp.service.MaintenanceWindowService;
import fynxt.common.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class MaintenanceWindowServiceImpl implements MaintenanceWindowService {

	private final MaintenanceWindowRepository maintenanceWindowRepository;

	@Override
	public boolean isPspInMaintenance(Psp psp, String flowActionId) {
		List<MaintenanceWindow> maintenanceWindows =
				maintenanceWindowRepository.findByPspIdAndFlowActionId(psp.getId(), flowActionId);

		if (CollectionUtils.isEmpty(maintenanceWindows)) {
			return false;
		}

		LocalDateTime now = LocalDateTime.now();
		boolean isInMaintenance = maintenanceWindows.stream()
				.anyMatch(window -> Status.ENABLED.equals(window.getStatus())
						&& window.getStartAt().isBefore(now)
						&& window.getEndAt().isAfter(now));

		return isInMaintenance;
	}

	@Override
	public List<Psp> filterPspsNotInMaintenance(List<Psp> psps, String flowActionId) {
		return psps.stream()
				.filter(psp -> !isPspInMaintenance(psp, flowActionId))
				.collect(Collectors.toList());
	}
}
