package fynxt.brand.auth.repository;

import fynxt.auth.enums.TokenStatus;
import fynxt.auth.enums.TokenType;
import fynxt.brand.auth.entity.Token;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByTokenHash(String tokenHash);

	@Query("SELECT t FROM Token t WHERE t.customerId = :customerId AND t.status = :status ORDER BY t.createdAt DESC")
	List<Token> findActiveByCustomerId(@Param("customerId") String customerId, @Param("status") TokenStatus status);

	@Query(
			"SELECT t FROM Token t WHERE t.customerId = :customerId AND t.tokenType = :tokenType AND t.status = :status ORDER BY t.createdAt DESC")
	List<Token> findActiveByCustomerIdAndTokenType(
			@Param("customerId") String customerId,
			@Param("tokenType") TokenType tokenType,
			@Param("status") TokenStatus status);

	@Query(
			"SELECT COUNT(t) FROM Token t WHERE t.tokenHash = :tokenHash AND t.status = :status AND t.tokenType = :tokenType")
	Long countActiveToken(
			@Param("tokenHash") String tokenHash,
			@Param("status") TokenStatus status,
			@Param("tokenType") TokenType tokenType);

	@Query("SELECT t FROM Token t WHERE t.expiresAt < :now AND t.status = :status")
	List<Token> findExpiredTokens(@Param("now") OffsetDateTime now, @Param("status") TokenStatus status);

	@Query("DELETE FROM Token t WHERE t.expiresAt < :expirationDate AND t.status IN ('EXPIRED', 'REVOKED')")
	void deleteExpiredTokens(@Param("expirationDate") OffsetDateTime expirationDate);
}
