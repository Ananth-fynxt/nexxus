package fynxt.brand.request.repository;

import fynxt.brand.request.entity.RequestPsp;
import fynxt.brand.request.entity.RequestPspId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestPspRepository extends JpaRepository<RequestPsp, RequestPspId> {

	List<RequestPsp> findByRequestId(@Param("requestId") UUID requestId);
}
