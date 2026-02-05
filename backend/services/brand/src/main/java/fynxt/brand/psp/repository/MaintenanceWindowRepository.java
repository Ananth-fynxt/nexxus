package fynxt.brand.psp.repository;

import fynxt.brand.psp.entity.MaintenanceWindow;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, Integer> {

	@Query("SELECT mw FROM MaintenanceWindow mw WHERE mw.deletedAt IS NULL AND mw.pspId = :pspId")
	List<MaintenanceWindow> findByPspId(@Param("pspId") UUID pspId);

	@Query(
			"SELECT mw FROM MaintenanceWindow mw WHERE mw.deletedAt IS NULL AND mw.pspId = :pspId AND mw.flowActionId = :flowActionId")
	List<MaintenanceWindow> findByPspIdAndFlowActionId(
			@Param("pspId") UUID pspId, @Param("flowActionId") String flowActionId);
}
