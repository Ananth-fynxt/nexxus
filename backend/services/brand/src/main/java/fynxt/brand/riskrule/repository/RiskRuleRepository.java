package fynxt.brand.riskrule.repository;

import fynxt.brand.riskrule.entity.EmbeddableRiskRuleId;
import fynxt.brand.riskrule.entity.RiskRule;
import fynxt.brand.riskrule.enums.RiskAction;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRule, EmbeddableRiskRuleId> {

	@Query(
			"SELECT r FROM RiskRule r WHERE r.deletedAt IS NULL AND r.riskRuleId.id = :id AND NOT EXISTS (SELECT 1 FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NOT NULL) ORDER BY r.riskRuleId.version DESC")
	List<RiskRule> findByIdOrderByVersionDesc(@Param("id") Integer id);

	@Query(
			"SELECT r FROM RiskRule r WHERE r.deletedAt IS NULL AND r.riskRuleId.id = :id AND NOT EXISTS (SELECT 1 FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NOT NULL) ORDER BY r.riskRuleId.version DESC LIMIT 1")
	Optional<RiskRule> findLatestVersionById(@Param("id") Integer id);

	@Query(
			"SELECT r FROM RiskRule r WHERE r.deletedAt IS NULL AND r.riskRuleId.id = :id AND r.riskRuleId.version = :version AND NOT EXISTS (SELECT 1 FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NOT NULL)")
	Optional<RiskRule> findByRiskRuleIdIdAndRiskRuleIdVersion(
			@Param("id") Integer id, @Param("version") Integer version);

	@Query(
			"SELECT r FROM RiskRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND r.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RiskRule r3 WHERE r3.riskRuleId.id = r.riskRuleId.id AND r3.deletedAt IS NOT NULL)")
	List<RiskRule> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);

	@Query(
			"SELECT COALESCE(MAX(r.riskRuleId.version), 0) FROM RiskRule r WHERE r.deletedAt IS NULL AND r.riskRuleId.id = :id AND NOT EXISTS (SELECT 1 FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NOT NULL)")
	Integer findMaxVersionById(@Param("id") Integer id);

	void deleteByRiskRuleIdId(Integer id);

	@Query(
			"SELECT COUNT(r) > 0 FROM RiskRule r WHERE r.deletedAt IS NULL AND r.brandId = :brandId AND r.environmentId = :environmentId AND r.flowActionId = :flowActionId AND r.name = :name AND r.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RiskRule r3 WHERE r3.riskRuleId.id = r.riskRuleId.id AND r3.deletedAt IS NOT NULL)")
	boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("name") String name);

	@Query(
			"SELECT rr FROM RiskRule rr JOIN RiskRulePsp rrp ON rr.riskRuleId.id = rrp.riskRuleId AND rr.riskRuleId.version = rrp.riskRuleVersion WHERE rr.deletedAt IS NULL AND rrp.pspId IN :pspIds AND rr.brandId = :brandId AND rr.environmentId = :environmentId AND rr.flowActionId = :flowActionId AND rr.currency = :currency AND rr.action = :action AND rr.status = :status AND rr.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = rr.riskRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RiskRule r3 WHERE r3.riskRuleId.id = rr.riskRuleId.id AND r3.deletedAt IS NOT NULL)")
	List<RiskRule> findLatestEnabledRiskRulesByCriteria(
			@Param("pspIds") List<UUID> pspIds,
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("flowActionId") String flowActionId,
			@Param("currency") String currency,
			@Param("action") RiskAction action,
			@Param("status") Status status);

	@Query(
			"SELECT rr FROM RiskRule rr JOIN RiskRulePsp rrp ON rr.riskRuleId.id = rrp.riskRuleId AND rr.riskRuleId.version = rrp.riskRuleVersion WHERE rr.deletedAt IS NULL AND rrp.pspId = :pspId AND rr.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = rr.riskRuleId.id AND r2.deletedAt IS NULL) AND NOT EXISTS (SELECT 1 FROM RiskRule r3 WHERE r3.riskRuleId.id = rr.riskRuleId.id AND r3.deletedAt IS NOT NULL)")
	List<RiskRule> findLatestRiskRulesByPspId(@Param("pspId") UUID pspId);

	@Query(value = "SELECT nextval('risk_rule_id_seq')", nativeQuery = true)
	Integer getNextId();
}
