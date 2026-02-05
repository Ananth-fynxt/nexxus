package fynxt.brand.psp.repository;

import fynxt.brand.psp.entity.Psp;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PspRepository extends JpaRepository<Psp, UUID> {

	@Query(
			"SELECT COUNT(p) > 0 FROM Psp p WHERE p.deletedAt IS NULL AND p.brandId = :brandId AND p.environmentId = :environmentId AND p.flowTargetId = :flowTargetId AND p.name = :name")
	boolean existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowTargetId") String flowTargetId,
			@Param("name") String name);

	@Query(
			"SELECT p FROM Psp p WHERE p.deletedAt IS NULL AND p.brandId = :brandId AND p.environmentId = :environmentId")
	List<Psp> findByBrandIdAndEnvironmentId(@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			value = "SELECT DISTINCT p.* FROM psps p "
					+ "INNER JOIN psp_operations po ON p.id = po.psp_id "
					+ "WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = CAST(:status AS status) AND po.status = CAST(:status AS status) "
					+ "AND :currency = ANY(po.currencies) AND po.flow_action_id = :flowActionId",
			nativeQuery = true)
	List<Psp> findByBrandEnvStatusCurrencyAndFlowAction(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("status") String status,
			@Param("currency") String currency,
			@Param("flowActionId") String flowActionId);

	@Query(
			value = "SELECT DISTINCT p.* FROM psps p "
					+ "INNER JOIN psp_operations po ON p.id = po.psp_id "
					+ "WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = CAST(:status AS status) AND po.status = CAST(:status AS status) "
					+ "AND po.flow_action_id = :flowActionId",
			nativeQuery = true)
	List<Psp> findByBrandEnvStatusAndFlowAction(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("status") String status,
			@Param("flowActionId") String flowActionId);

	@Query(
			value =
					"SELECT DISTINCT UNNEST(po.currencies) FROM psp_operations po JOIN psps p ON po.psp_id = p.id WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId",
			nativeQuery = true)
	List<String> findSupportedCurrenciesByBrandAndEnvironment(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			value =
					"SELECT DISTINCT UNNEST(po.countries) FROM psp_operations po JOIN psps p ON po.psp_id = p.id WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId",
			nativeQuery = true)
	List<String> findSupportedCountriesByBrandAndEnvironment(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			value =
					"SELECT DISTINCT p.* FROM psps p JOIN psp_operations po ON p.id = po.psp_id WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId AND po.flow_action_id = :actionId AND p.status = 'ENABLED' AND po.status = 'ENABLED'",
			nativeQuery = true)
	List<Psp> findActivePspsByBrandEnvironmentAndAction(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("actionId") String actionId);

	@Query(
			value =
					"SELECT DISTINCT p.* FROM psps p JOIN psp_operations po ON p.id = po.psp_id WHERE p.deleted_at IS NULL AND p.brand_id = :brandId AND p.environment_id = :environmentId AND po.flow_action_id = :actionId AND :currency = ANY(po.currencies) AND p.status = 'ENABLED' AND po.status = 'ENABLED'",
			nativeQuery = true)
	List<Psp> findActivePspsByBrandEnvironmentActionAndCurrency(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("actionId") String actionId,
			@Param("currency") String currency);
}
