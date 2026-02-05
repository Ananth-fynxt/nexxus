package fynxt.brand.session.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "brand_id", nullable = false)
	private UUID brandId;

	@Column(name = "environment_id", nullable = false)
	private UUID environmentId;

	@Column(name = "txn_id", nullable = false)
	private String txnId;

	@Column(name = "txn_version", nullable = false)
	private Integer txnVersion;

	@Column(name = "session_token_hash", nullable = false, length = 512)
	private String sessionTokenHash;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "last_accessed_at")
	private Instant lastAccessedAt;

	@Column(name = "timeout_minutes", nullable = false)
	@Builder.Default
	private Integer timeoutMinutes = 5;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	private Integer createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", nullable = false)
	private Integer updatedBy;
}
