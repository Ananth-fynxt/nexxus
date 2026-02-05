package fynxt.brand.psp.service;

import fynxt.brand.psp.entity.Psp;

import java.util.List;

public interface MaintenanceWindowService {
	boolean isPspInMaintenance(Psp psp, String flowActionId);

	List<Psp> filterPspsNotInMaintenance(List<Psp> psps, String flowActionId);
}
