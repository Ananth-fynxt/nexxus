package fynxt.brand.transactionlimit.repository;

import fynxt.brand.transactionlimit.entity.EmbeddableTransactionLimitId;
import fynxt.brand.transactionlimit.entity.TransactionLimit;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, EmbeddableTransactionLimitId> {

	@Query(
			"SELECT tl FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.brandId = :brandId AND tl.environmentId = :environmentId AND tl.name = :name AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl3 WHERE tl3.transactionLimitId.id = tl.transactionLimitId.id AND tl3.deletedAt IS NOT NULL)")
	Optional<TransactionLimit> findByBrandIdAndEnvironmentIdAndName(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("name") String name);

	@Query(
			"SELECT tl FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.brandId = :brandId AND tl.environmentId = :environmentId AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl3 WHERE tl3.transactionLimitId.id = tl.transactionLimitId.id AND tl3.deletedAt IS NOT NULL)")
	List<TransactionLimit> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT tl FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.transactionLimitId.id = :id AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NOT NULL) ORDER BY tl.transactionLimitId.version DESC")
	List<TransactionLimit> findByIdOrderByVersionDesc(@Param("id") Integer id);

	@Query(
			"SELECT tl FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.transactionLimitId.id = :id AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NOT NULL) ORDER BY tl.transactionLimitId.version DESC LIMIT 1")
	Optional<TransactionLimit> findLatestVersionById(@Param("id") Integer id);

	@Query(
			"SELECT tl FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.transactionLimitId.id = :id AND tl.transactionLimitId.version = :version AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NOT NULL)")
	Optional<TransactionLimit> findByTransactionLimitIdIdAndTransactionLimitIdVersion(
			@Param("id") Integer id, @Param("version") Integer version);

	@Query(
			"SELECT COALESCE(MAX(tl.transactionLimitId.version), 0) FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.transactionLimitId.id = :id AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NOT NULL)")
	Integer findMaxVersionById(@Param("id") Integer id);

	void deleteByTransactionLimitIdId(Integer id);

	@Query(
			"SELECT COUNT(tl) > 0 FROM TransactionLimit tl WHERE tl.deletedAt IS NULL AND tl.brandId = :brandId AND tl.environmentId = :environmentId AND tl.name = :name AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl3 WHERE tl3.transactionLimitId.id = tl.transactionLimitId.id AND tl3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndName(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("name") String name);

	@Query(
			"SELECT tl FROM TransactionLimit tl JOIN TransactionLimitPsp tlp ON tl.transactionLimitId.id = tlp.transactionLimitId AND tl.transactionLimitId.version = tlp.transactionLimitVersion JOIN TransactionLimitPspAction tlpa ON tl.transactionLimitId.id = tlpa.transactionLimitId AND tl.transactionLimitId.version = tlpa.transactionLimitVersion WHERE tl.deletedAt IS NULL AND tlp.pspId IN :pspIds AND tl.brandId = :brandId AND tl.environmentId = :environmentId AND tlpa.flowActionId = :flowActionId AND tl.currency = :currency AND tl.status = :status AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id AND tl2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM TransactionLimit tl3 WHERE tl3.transactionLimitId.id = tl.transactionLimitId.id AND tl3.deletedAt IS NOT NULL)")
	List<TransactionLimit> findLatestEnabledTransactionLimitsByCriteria(
			@Param("pspIds") List<UUID> pspIds,
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("currency") String currency,
			@Param("status") Status status);

	@Query(value = "SELECT nextval('transaction_limits_id_seq')", nativeQuery = true)
	Integer getNextId();
}
