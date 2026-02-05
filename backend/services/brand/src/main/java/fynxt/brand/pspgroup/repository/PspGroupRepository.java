package fynxt.brand.pspgroup.repository;

import fynxt.brand.pspgroup.entity.EmbeddablePspGroupId;
import fynxt.brand.pspgroup.entity.PspGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PspGroupRepository extends JpaRepository<PspGroup, EmbeddablePspGroupId> {

	@Query(value = "SELECT nextval('psp_groups_id_seq')", nativeQuery = true)
	Integer getNextId();

	@Query(
			"SELECT pg FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.brandId = :brandId AND pg.environmentId = :environmentId AND pg.flowActionId = :flowActionId AND pg.name = :name AND pg.pspGroupId.version = (SELECT MAX(pg2.pspGroupId.version) FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM PspGroup pg3 WHERE pg3.pspGroupId.id = pg.pspGroupId.id AND pg3.deletedAt IS NOT NULL)")
	Optional<PspGroup> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("name") String name);

	@Query(
			"SELECT pg FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.brandId = :brandId AND pg.environmentId = :environmentId AND pg.pspGroupId.version = (SELECT MAX(pg2.pspGroupId.version) FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM PspGroup pg3 WHERE pg3.pspGroupId.id = pg.pspGroupId.id AND pg3.deletedAt IS NOT NULL)")
	List<PspGroup> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT pg FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.pspGroupId.id = :id AND NOT EXISTS (SELECT 1 FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NOT NULL) ORDER BY pg.pspGroupId.version DESC")
	List<PspGroup> findByIdOrderByVersionDesc(@Param("id") Integer id);

	@Query(
			"SELECT pg FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.pspGroupId.id = :id AND NOT EXISTS (SELECT 1 FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NOT NULL) ORDER BY pg.pspGroupId.version DESC LIMIT 1")
	Optional<PspGroup> findLatestVersionById(@Param("id") Integer id);

	@Query(
			"SELECT pg FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.pspGroupId.id = :id AND pg.pspGroupId.version = :version AND NOT EXISTS (SELECT 1 FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NOT NULL)")
	Optional<PspGroup> findByPspGroupIdIdAndPspGroupIdVersion(
			@Param("id") Integer id, @Param("version") Integer version);

	@Query(
			"SELECT COALESCE(MAX(pg.pspGroupId.version), 0) FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.pspGroupId.id = :id AND NOT EXISTS (SELECT 1 FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NOT NULL)")
	Integer findMaxVersionById(@Param("id") Integer id);

	void deleteByPspGroupIdId(Integer id);

	@Query(
			"SELECT COUNT(pg) > 0 FROM PspGroup pg WHERE pg.deletedAt IS NULL AND pg.brandId = :brandId AND pg.environmentId = :environmentId AND pg.flowActionId = :flowActionId AND pg.name = :name AND pg.pspGroupId.version = (SELECT MAX(pg2.pspGroupId.version) FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id AND pg2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM PspGroup pg3 WHERE pg3.pspGroupId.id = pg.pspGroupId.id AND pg3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("name") String name);
}
