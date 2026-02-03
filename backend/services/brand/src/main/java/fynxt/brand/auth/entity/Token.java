package fynxt.brand.auth.entity;

import fynxt.auth.enums.TokenStatus;
import fynxt.auth.enums.TokenType;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tokens")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "customer_id", nullable = false)
	private String customerId;

	@Column(name = "token_hash", nullable = false, unique = true, length = 500)
	private String tokenHash;

	@Column(name = "issued_at", nullable = false)
	private OffsetDateTime issuedAt;

	@Column(name = "expires_at", nullable = false)
	private OffsetDateTime expiresAt;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.auth.enums.TokenStatus"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "token_status")
	@Builder.Default
	private TokenStatus status = TokenStatus.ACTIVE;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.auth.enums.TokenType"))
	@Enumerated(EnumType.STRING)
	@Column(name = "token_type", columnDefinition = "token_type")
	private TokenType tokenType;

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
