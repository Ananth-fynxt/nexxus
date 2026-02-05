package fynxt.brand.request.repository;

import fynxt.brand.request.entity.RequestTransactionLimit;
import fynxt.brand.request.entity.RequestTransactionLimitId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTransactionLimitRepository
		extends JpaRepository<RequestTransactionLimit, RequestTransactionLimitId> {

	List<RequestTransactionLimit> findByRequestId(@Param("requestId") UUID requestId);
}
