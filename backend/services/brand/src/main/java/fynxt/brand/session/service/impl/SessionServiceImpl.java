package fynxt.brand.session.service.impl;

import fynxt.brand.enums.ErrorCode;
import fynxt.brand.session.dto.TransactionSession;
import fynxt.brand.session.entity.Session;
import fynxt.brand.session.repository.SessionRepository;
import fynxt.brand.session.service.SessionService;
import fynxt.brand.session.service.util.TokenUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

	private final SessionRepository sessionRepository;
	private final ObjectMapper objectMapper;

	@Value("${session.hmac-key}")
	private String hmacKey;

	private static final int DEFAULT_TIMEOUT_MINUTES = 5;

	@Override
	@Transactional
	public String createSessionFromTransaction(
			Object transactionData,
			UUID brandId,
			UUID environmentId,
			String txnId,
			Integer txnVersion,
			Integer pspTimeoutSeconds) {

		sessionRepository
				.findByBrandIdAndEnvironmentIdAndTxnIdAndTxnVersion(brandId, environmentId, txnId, txnVersion)
				.ifPresent(existing -> {
					throw new IllegalStateException(
							"Session already exists for transaction: " + txnId + " (version: " + txnVersion + ")");
				});

		String sessionToken = generateSessionTokenFromTransactionData(transactionData);
		String sessionTokenHash = TokenUtils.hmacSha256(hmacKey, sessionToken);

		Integer timeoutMinutes = calculateTimeoutMinutes(pspTimeoutSeconds);

		Instant expiresAt = Instant.now().plus(timeoutMinutes, ChronoUnit.MINUTES);

		Session session = Session.builder()
				.brandId(brandId)
				.environmentId(environmentId)
				.txnId(txnId)
				.txnVersion(txnVersion)
				.sessionTokenHash(sessionTokenHash)
				.expiresAt(expiresAt)
				.timeoutMinutes(timeoutMinutes)
				.build();

		sessionRepository.save(session);

		return sessionToken;
	}

	private String generateSessionTokenFromTransactionData(Object transactionData) {
		if (transactionData == null) {
			return TokenUtils.generateSessionToken();
		}

		String dataJson = null;
		try {
			dataJson = objectMapper.writeValueAsString(transactionData);
		} catch (Exception e) {
			return TokenUtils.generateSessionToken();
		}
		return TokenUtils.generateSessionTokenFromData(dataJson);
	}

	private Integer calculateTimeoutMinutes(Integer pspTimeoutSeconds) {
		if (pspTimeoutSeconds == null || pspTimeoutSeconds <= 0) {
			return DEFAULT_TIMEOUT_MINUTES;
		}

		int timeoutMinutes = (int) Math.ceil(pspTimeoutSeconds / 60.0);
		if (timeoutMinutes < 1) {
			timeoutMinutes = DEFAULT_TIMEOUT_MINUTES;
		}

		return timeoutMinutes;
	}

	@Override
	public TransactionSession getTransactionResponseBySessionToken(String sessionToken) {

		String sessionTokenHash = TokenUtils.hmacSha256(hmacKey, sessionToken);
		Session session = sessionRepository
				.findBySessionTokenHash(sessionTokenHash)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.SESSION_NOT_FOUND.getCode()));

		if (Instant.now().isAfter(session.getExpiresAt())) {
			throw new ResponseStatusException(HttpStatus.GONE, ErrorCode.SESSION_EXPIRED.getCode());
		}

		session.setLastAccessedAt(Instant.now());
		sessionRepository.save(session);

		// Decode the transactionData from the session token
		Object decodedTransactionData = null;
		try {
			String decodedDataJson = TokenUtils.decodeSessionTokenData(sessionToken);
			decodedTransactionData = objectMapper.readValue(decodedDataJson, Object.class);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.SESSION_INVALID_TOKEN.getCode());
		}

		return TransactionSession.builder()
				.txnId(session.getTxnId())
				.txnData(decodedTransactionData)
				.build();
	}

	@Override
	public boolean validateSession(String sessionToken) {

		try {
			String sessionTokenHash = TokenUtils.hmacSha256(hmacKey, sessionToken);
			Session session = sessionRepository
					.findBySessionTokenHash(sessionTokenHash)
					.orElseThrow(() ->
							new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.SESSION_NOT_FOUND.getCode()));

			if (Instant.now().isAfter(session.getExpiresAt())) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
