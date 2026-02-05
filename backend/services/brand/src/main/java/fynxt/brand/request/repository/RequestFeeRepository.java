package fynxt.brand.request.repository;

import fynxt.brand.request.entity.RequestFee;
import fynxt.brand.request.entity.RequestFeeId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestFeeRepository extends JpaRepository<RequestFee, RequestFeeId> {

	List<RequestFee> findByRequestId(@Param("requestId") UUID requestId);
}
