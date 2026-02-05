package fynxt.brand.psp.repository;

import fynxt.brand.psp.entity.PspOperation;
import fynxt.brand.psp.entity.PspOperationId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PspOperationRepository extends JpaRepository<PspOperation, PspOperationId> {

	@Query(
			value =
					"SELECT * FROM psp_operations WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId",
			nativeQuery = true)
	PspOperation findByCompositeKey(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("pspId") UUID pspId,
			@Param("flowActionId") String flowActionId,
			@Param("flowDefinitionId") String flowDefinitionId);

	@Modifying
	@Query(
			value =
					"INSERT INTO psp_operations (brand_id, environment_id, psp_id, flow_action_id, flow_definition_id, status) VALUES (:brandId, :environmentId, :pspId, :flowActionId, :flowDefinitionId, :status)",
			nativeQuery = true)
	void insertPspOperation(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("pspId") UUID pspId,
			@Param("flowActionId") String flowActionId,
			@Param("flowDefinitionId") String flowDefinitionId,
			@Param("status") String status);

	@Modifying
	@Query(
			value =
					"UPDATE psp_operations SET status = :status WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId",
			nativeQuery = true)
	void updateStatus(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("pspId") UUID pspId,
			@Param("flowActionId") String flowActionId,
			@Param("flowDefinitionId") String flowDefinitionId,
			@Param("status") String status);

	@Query(value = "SELECT * FROM psp_operations WHERE psp_id = :pspId", nativeQuery = true)
	List<PspOperation> findByPspId(@Param("pspId") UUID pspId);

	@Modifying
	@Query(value = "DELETE FROM psp_operations WHERE psp_id = :pspId", nativeQuery = true)
	void deleteByPspId(@Param("pspId") UUID pspId);

	@Query(
			value =
					"SELECT COUNT(*) FROM psp_operations WHERE psp_id IN :pspIds AND flow_action_id = :flowActionId AND :currency = ANY(currencies)",
			nativeQuery = true)
	long countByPspIdsAndFlowActionIdAndCurrency(
			@Param("pspIds") List<UUID> pspIds,
			@Param("flowActionId") String flowActionId,
			@Param("currency") String currency);

	PspOperation findByPspIdAndFlowActionId(UUID pspId, String flowActionId);
}
