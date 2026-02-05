package fynxt.brand.webhook.repository;

import fynxt.brand.webhook.entity.Webhook;
import fynxt.brand.webhook.enums.WebhookStatusType;
import fynxt.common.enums.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Short> {

	@Query("SELECT w FROM Webhook w WHERE w.deletedAt IS NULL AND w.id = :id")
	@Override
	Optional<Webhook> findById(Short id);

	@Query(
			"SELECT w FROM Webhook w WHERE w.deletedAt IS NULL AND w.brandId = :brandId AND w.environmentId = :environmentId AND w.statusType = :statusType")
	Optional<Webhook> findByBrandIdAndEnvironmentIdAndStatusType(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("statusType") WebhookStatusType statusType);

	@Query(
			"SELECT COUNT(w) > 0 FROM Webhook w WHERE w.deletedAt IS NULL AND w.brandId = :brandId AND w.environmentId = :environmentId AND w.statusType = :statusType")
	boolean existsByBrandIdAndEnvironmentIdAndStatusType(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("statusType") WebhookStatusType statusType);

	@Query(
			"SELECT w FROM Webhook w WHERE w.deletedAt IS NULL AND w.brandId = :brandId AND w.environmentId = :environmentId AND w.statusType = :statusType AND w.status = :status")
	List<Webhook> findByBrandIdAndEnvironmentIdAndStatusTypeAndStatus(
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("statusType") WebhookStatusType statusType,
			@Param("status") Status status);

	@Query(
			"SELECT w FROM Webhook w WHERE w.deletedAt IS NULL AND w.brandId = :brandId AND w.environmentId = :environmentId ORDER BY w.updatedAt DESC")
	List<Webhook> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);
}
