package fynxt.brand.transaction.entity;

import java.security.SecureRandom;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class TransactionIdGenerator implements IdentifierGenerator {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String ALPHANUMERIC_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int TRANSACTION_ID_LENGTH = 12;
	public static final String TRANSACTION = "ortx";

	@Override
	public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

		if (o instanceof Transaction txn) {
			if (txn.getId() != null
					&& txn.getId().getTxnId() != null
					&& !txn.getId().getTxnId().isEmpty()) {
				return txn.getId().getTxnId();
			}
		}

		return generateTransactionId();
	}

	private String generateTransactionId() {
		String randomId = generateRandomId(TRANSACTION_ID_LENGTH);
		return TRANSACTION + randomId;
	}

	private String generateRandomId(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
			sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
		}
		return sb.toString();
	}
}
