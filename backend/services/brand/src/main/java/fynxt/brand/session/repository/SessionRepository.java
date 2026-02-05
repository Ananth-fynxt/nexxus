package fynxt.brand.session.repository;

import fynxt.brand.session.entity.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

	Optional<Session> findByIdAndBrandIdAndEnvironmentId(Long id, UUID brandId, UUID environmentId);

	Optional<Session> findBySessionTokenHash(String sessionTokenHash);

	Optional<Session> findByBrandIdAndEnvironmentIdAndTxnIdAndTxnVersion(
			UUID brandId, UUID environmentId, String txnId, Integer txnVersion);

	@Query(
			"SELECT s FROM Session s WHERE s.brandId = :brandId AND s.environmentId = :environmentId AND s.expiresAt > :now")
	java.util.List<Session> findActiveSessionsByBrandAndEnvironment(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("now") Instant now);
}
