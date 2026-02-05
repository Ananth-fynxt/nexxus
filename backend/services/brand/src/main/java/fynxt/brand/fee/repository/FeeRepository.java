package fynxt.brand.fee.repository;

import fynxt.brand.fee.entity.EmbeddableFeeId;
import fynxt.brand.fee.entity.Fee;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, EmbeddableFeeId> {

	@Query(
			"SELECT f FROM Fee f WHERE f.deletedAt IS NULL AND f.brandId = :brandId AND f.environmentId = :environmentId AND f.flowActionId = :flowActionId AND f.name = :name AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM Fee f3 WHERE f3.feeId.id = f.feeId.id AND f3.deletedAt IS NOT NULL)")
	Optional<Fee> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("name") String name);

	@Query(
			"SELECT f FROM Fee f WHERE f.deletedAt IS NULL AND f.brandId = :brandId AND f.environmentId = :environmentId AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM Fee f3 WHERE f3.feeId.id = f.feeId.id AND f3.deletedAt IS NOT NULL)")
	List<Fee> findByBrandIdAndEnvironmentId(@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT f FROM Fee f WHERE f.deletedAt IS NULL AND f.feeId.id = :id AND NOT EXISTS (SELECT 1 FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NOT NULL) ORDER BY f.feeId.version DESC")
	List<Fee> findByIdOrderByVersionDesc(@Param("id") Integer id);

	@Query(
			"SELECT f FROM Fee f WHERE f.deletedAt IS NULL AND f.feeId.id = :id AND NOT EXISTS (SELECT 1 FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NOT NULL) ORDER BY f.feeId.version DESC LIMIT 1")
	Optional<Fee> findLatestVersionById(@Param("id") Integer id);

	@Query(
			"SELECT f FROM Fee f WHERE f.deletedAt IS NULL AND f.feeId.id = :id AND f.feeId.version = :version AND NOT EXISTS (SELECT 1 FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NOT NULL)")
	Optional<Fee> findByFeeIdIdAndFeeIdVersion(@Param("id") Integer id, @Param("version") Integer version);

	@Query(
			"SELECT COALESCE(MAX(f.feeId.version), 0) FROM Fee f WHERE f.deletedAt IS NULL AND f.feeId.id = :id AND NOT EXISTS (SELECT 1 FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NOT NULL)")
	Integer findMaxVersionById(@Param("id") Integer id);

	void deleteByFeeIdId(Integer id);

	@Query(
			"SELECT COUNT(f) > 0 FROM Fee f WHERE f.deletedAt IS NULL AND f.brandId = :brandId AND f.environmentId = :environmentId AND f.flowActionId = :flowActionId AND f.name = :name AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM Fee f3 WHERE f3.feeId.id = f.feeId.id AND f3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("name") String name);

	@Query(
			"SELECT f FROM Fee f JOIN FeePsp fp ON f.feeId.id = fp.feeId AND f.feeId.version = fp.feeVersion WHERE f.deletedAt IS NULL AND fp.pspId IN :pspIds AND f.brandId = :brandId AND f.environmentId = :environmentId AND f.flowActionId = :flowActionId AND f.currency = :currency AND f.status = :status AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM Fee f3 WHERE f3.feeId.id = f.feeId.id AND f3.deletedAt IS NOT NULL)")
	List<Fee> findLatestEnabledFeeRulesByCriteria(
			@Param("pspIds") List<UUID> pspIds,
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("currency") String currency,
			@Param("status") Status status);

	@Query(
			"SELECT f FROM Fee f JOIN FeePsp fp ON f.feeId.id = fp.feeId AND f.feeId.version = fp.feeVersion WHERE f.deletedAt IS NULL AND fp.pspId = :pspId AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id AND f2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM Fee f3 WHERE f3.feeId.id = f.feeId.id AND f3.deletedAt IS NOT NULL)")
	List<Fee> findLatestFeesByPspId(@Param("pspId") UUID pspId);

	@Query(value = "SELECT nextval('fee_id_seq')", nativeQuery = true)
	Integer getNextId();
}
