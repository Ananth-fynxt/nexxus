package fynxt.brand.session.service;

import fynxt.brand.session.dto.TransactionSession;

import java.util.UUID;

public interface SessionService {

	String createSessionFromTransaction(
			Object transactionData,
			UUID brandId,
			UUID environmentId,
			String txnId,
			Integer txnVersion,
			Integer pspTimeoutSeconds);

	TransactionSession getTransactionResponseBySessionToken(String sessionToken);

	boolean validateSession(String sessionToken);
}
