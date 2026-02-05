package fynxt.brand.fee.repository;

import fynxt.brand.fee.entity.EmbeddableFeeComponentId;
import fynxt.brand.fee.entity.FeeComponent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeComponentRepository extends JpaRepository<FeeComponent, EmbeddableFeeComponentId> {

	List<FeeComponent> findByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(Integer feeId, Integer feeVersion);

	void deleteByFeeComponentIdFeeId(Integer feeId);

	void deleteByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(Integer feeId, Integer feeVersion);
}
