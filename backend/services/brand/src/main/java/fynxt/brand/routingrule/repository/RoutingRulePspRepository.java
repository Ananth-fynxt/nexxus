package fynxt.brand.routingrule.repository;

import fynxt.brand.routingrule.entity.RoutingRulePsp;
import fynxt.brand.routingrule.entity.RoutingRulePspId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingRulePspRepository extends JpaRepository<RoutingRulePsp, RoutingRulePspId> {

	@Modifying
	@Query("DELETE FROM RoutingRulePsp r WHERE r.routingRuleId = :routingRuleId")
	void deleteAllByRoutingRuleId(@Param("routingRuleId") Integer routingRuleId);

	@Query(
			"SELECT r FROM RoutingRulePsp r WHERE r.routingRuleId = :routingRuleId AND r.routingRuleVersion = :routingRuleVersion")
	List<RoutingRulePsp> findByRoutingRuleIdAndRoutingRuleVersion(
			@Param("routingRuleId") Integer routingRuleId, @Param("routingRuleVersion") Integer routingRuleVersion);
}
