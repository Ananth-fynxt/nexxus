package fynxt.brand.environment.entity;

import fynxt.database.audit.AuditingEntity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "environments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Environment extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "secret", nullable = false, unique = true)
	private UUID secret;

	@Column(name = "token", nullable = false, unique = true)
	private UUID token;

	@Column(name = "origin")
	private String origin;

	@Column(name = "success_redirect_url")
	private String successRedirectUrl;

	@Column(name = "failure_redirect_url")
	private String failureRedirectUrl;

	@Column(name = "brand_id", nullable = false)
	private UUID brandId;
}
