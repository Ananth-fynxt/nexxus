package fynxt.brand.riskrule.repository;

import fynxt.brand.riskrule.entity.RiskRulePsp;
import fynxt.brand.riskrule.entity.RiskRulePspId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskRulePspRepository extends JpaRepository<RiskRulePsp, RiskRulePspId> {

	List<RiskRulePsp> findByRiskRuleIdAndRiskRuleVersion(Integer riskRuleId, Integer riskRuleVersion);

	List<RiskRulePsp> findByPspId(UUID pspId);

	@Query("SELECT DISTINCT r.riskRuleId FROM RiskRulePsp r WHERE r.pspId = :pspId")
	List<Integer> findRiskRuleIdsByPspId(@Param("pspId") UUID pspId);

	@Query("SELECT DISTINCT r.riskRuleId FROM RiskRulePsp r WHERE r.pspId IN :pspIds")
	List<Integer> findRiskRuleIdsByPspIds(@Param("pspIds") List<UUID> pspIds);

	/**
	 * Find the most recent version of RiskRulePsp records for each riskRuleId associated with the
	 * given pspId. This query uses a window function to get the maximum version for each riskRuleId
	 * and then joins back to get the complete RiskRulePsp records.
	 */
	@Query(
			value = "SELECT rrp.* FROM risk_rule_psps rrp "
					+ "INNER JOIN ( "
					+ "  SELECT risk_rule_id, MAX(risk_rule_version) as max_version "
					+ "  FROM risk_rule_psps "
					+ "  WHERE psp_id = :pspId "
					+ "  GROUP BY risk_rule_id "
					+ ") latest ON rrp.risk_rule_id = latest.risk_rule_id AND rrp.risk_rule_version = latest.max_version "
					+ "WHERE rrp.psp_id = :pspId",
			nativeQuery = true)
	List<RiskRulePsp> findLatestRiskRulePspsByPspId(@Param("pspId") UUID pspId);

	/**
	 * Find the most recent version of RiskRulePsp records for each riskRuleId associated with the
	 * given pspIds. This query uses a window function to get the maximum version for each riskRuleId
	 * and then joins back to get the complete RiskRulePsp records.
	 */
	@Query(
			value = "SELECT rrp.* FROM risk_rule_psps rrp "
					+ "INNER JOIN ( "
					+ "  SELECT risk_rule_id, MAX(risk_rule_version) as max_version "
					+ "  FROM risk_rule_psps "
					+ "  WHERE psp_id IN :pspIds "
					+ "  GROUP BY risk_rule_id "
					+ ") latest ON rrp.risk_rule_id = latest.risk_rule_id AND rrp.risk_rule_version = latest.max_version "
					+ "WHERE rrp.psp_id IN :pspIds",
			nativeQuery = true)
	List<RiskRulePsp> findLatestRiskRulePspsByPspIds(@Param("pspIds") List<UUID> pspIds);

	void deleteByRiskRuleIdAndRiskRuleVersion(Integer riskRuleId, Integer riskRuleVersion);

	void deleteByRiskRuleId(Integer riskRuleId);
}
