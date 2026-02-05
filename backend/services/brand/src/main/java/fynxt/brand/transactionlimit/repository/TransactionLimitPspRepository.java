package fynxt.brand.transactionlimit.repository;

import fynxt.brand.transactionlimit.entity.TransactionLimitPsp;
import fynxt.brand.transactionlimit.entity.TransactionLimitPspId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLimitPspRepository extends JpaRepository<TransactionLimitPsp, TransactionLimitPspId> {

	List<TransactionLimitPsp> findByTransactionLimitIdAndTransactionLimitVersion(
			Integer transactionLimitId, Integer transactionLimitVersion);

	List<TransactionLimitPsp> findByPspId(UUID pspId);

	@Query(
			"SELECT tlp FROM TransactionLimitPsp tlp WHERE tlp.pspId = :pspId AND tlp.transactionLimitVersion = (SELECT MAX(tlp2.transactionLimitVersion) FROM TransactionLimitPsp tlp2 WHERE tlp2.transactionLimitId = tlp.transactionLimitId AND tlp2.pspId = :pspId)")
	List<TransactionLimitPsp> findLatestVersionsByPspId(@Param("pspId") UUID pspId);

	void deleteByTransactionLimitId(Integer transactionLimitId);

	void deleteByTransactionLimitIdAndTransactionLimitVersion(
			Integer transactionLimitId, Integer transactionLimitVersion);
}
