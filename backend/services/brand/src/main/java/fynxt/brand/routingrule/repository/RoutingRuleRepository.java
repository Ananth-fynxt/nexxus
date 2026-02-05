package fynxt.brand.routingrule.repository;

import fynxt.brand.routingrule.entity.EmbeddableRoutingRuleId;
import fynxt.brand.routingrule.entity.RoutingRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingRuleRepository extends JpaRepository<RoutingRule, EmbeddableRoutingRuleId> {

	@Query(
			"SELECT r FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.routingRuleId.id = :id AND NOT EXISTS (SELECT 1 FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NOT NULL) ORDER BY r.routingRuleId.version DESC LIMIT 1")
	Optional<RoutingRule> findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(@Param("id") Integer id);

	@Query(
			value =
					"SELECT * FROM (SELECT r.*, ROW_NUMBER() OVER (PARTITION BY r.id ORDER BY r.version DESC) as rn FROM routing_rules r WHERE r.deleted_at IS NULL AND r.brand_id = :brandId AND r.environment_id = :environmentId AND NOT EXISTS (SELECT 1 FROM routing_rules r2 WHERE r2.id = r.id AND r2.deleted_at IS NOT NULL)) ranked WHERE rn = 1",
			nativeQuery = true)
	List<RoutingRule> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT COUNT(DISTINCT r.routingRuleId.id) FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND NOT EXISTS (SELECT 1 FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NOT NULL)")
	Long countByBrandIdAndEnvironmentId(@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT COUNT(DISTINCT r.routingRuleId.id) FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND r.routingRuleId.id != :excludeId AND NOT EXISTS (SELECT 1 FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NOT NULL)")
	Long countByBrandIdAndEnvironmentIdExcludingId(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("excludeId") Integer excludeId);

	@Query(
			value =
					"SELECT r.* FROM routing_rules r WHERE r.deleted_at IS NULL AND r.id = :routingRuleId AND r.status = 'ENABLED' AND r.version = (SELECT MAX(r2.version) FROM routing_rules r2 WHERE r2.deleted_at IS NULL AND r2.id = :routingRuleId) AND NOT EXISTS (SELECT 1 FROM routing_rules r3 WHERE r3.id = r.id AND r3.deleted_at IS NOT NULL) LIMIT 1",
			nativeQuery = true)
	RoutingRule findActiveRoutingRuleById(@Param("routingRuleId") Integer routingRuleId);

	@Query(
			value = "SELECT r.* FROM routing_rules r "
					+ "WHERE r.deleted_at IS NULL AND r.brand_id = :brandId "
					+ "AND r.environment_id = :environmentId "
					+ "AND r.status = 'ENABLED' "
					+ "AND r.version = (SELECT MAX(r2.version) FROM routing_rules r2 WHERE r2.deleted_at IS NULL AND r2.id = r.id) "
					+ "AND NOT EXISTS (SELECT 1 FROM routing_rules r3 WHERE r3.id = r.id AND r3.deleted_at IS NOT NULL) "
					+ "ORDER BY r.created_at DESC",
			nativeQuery = true)
	List<RoutingRule> findEnabledRoutingRulesByBrandAndEnvironment(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT r FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.routingRuleId = :routingRuleId AND NOT EXISTS (SELECT 1 FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NOT NULL)")
	Optional<RoutingRule> findByRoutingRuleId(EmbeddableRoutingRuleId routingRuleId);

	void deleteByRoutingRuleIdId(Integer id);

	@Query(
			"SELECT COUNT(r) > 0 FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND r.name = :name AND r.routingRuleId.version = (SELECT MAX(r2.routingRuleId.version) FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RoutingRule r3 WHERE r3.routingRuleId.id = r.routingRuleId.id AND r3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndName(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("name") String name);

	@Query(
			"SELECT COUNT(r) > 0 FROM RoutingRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND r.name = :name AND r.routingRuleId.id != :excludeId AND r.routingRuleId.version = (SELECT MAX(r2.routingRuleId.version) FROM RoutingRule r2 WHERE r2.routingRuleId.id = r.routingRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RoutingRule r3 WHERE r3.routingRuleId.id = r.routingRuleId.id AND r3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndNameAndIdNot(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("name") String name,
			@Param("excludeId") Integer excludeId);

	@Query(value = "SELECT nextval('routing_rules_id_seq')", nativeQuery = true)
	Integer getNextId();
}
