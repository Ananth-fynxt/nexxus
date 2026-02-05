package fynxt.brand.transactionlimit.repository;

import fynxt.brand.transactionlimit.entity.TransactionLimitPspAction;
import fynxt.brand.transactionlimit.entity.TransactionLimitPspActionId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLimitPspActionRepository
		extends JpaRepository<TransactionLimitPspAction, TransactionLimitPspActionId> {

	List<TransactionLimitPspAction> findByTransactionLimitIdAndTransactionLimitVersion(
			Integer transactionLimitId, Integer transactionLimitVersion);

	void deleteByTransactionLimitId(Integer transactionLimitId);

	void deleteByTransactionLimitIdAndTransactionLimitVersion(
			Integer transactionLimitId, Integer transactionLimitVersion);
}
