package fynxt.brand.request.repository;

import fynxt.brand.request.entity.RequestRiskRule;
import fynxt.brand.request.entity.RequestRiskRuleId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRiskRuleRepository extends JpaRepository<RequestRiskRule, RequestRiskRuleId> {

	List<RequestRiskRule> findByRequestId(@Param("requestId") UUID requestId);
}
