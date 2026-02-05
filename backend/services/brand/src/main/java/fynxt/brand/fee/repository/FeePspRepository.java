package fynxt.brand.fee.repository;

import fynxt.brand.fee.entity.FeePsp;
import fynxt.brand.fee.entity.FeePspId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeePspRepository extends JpaRepository<FeePsp, FeePspId> {

	List<FeePsp> findByFeeIdAndFeeVersion(Integer feeId, Integer feeVersion);

	List<FeePsp> findByPspId(UUID pspId);

	void deleteByFeeId(Integer feeId);

	void deleteByFeeIdAndFeeVersion(Integer feeId, Integer feeVersion);
}
