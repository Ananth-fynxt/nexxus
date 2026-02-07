package fynxt.brand.brandrole.entity;

import fynxt.database.audit.AuditingEntity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "brand_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandRole extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "brand_id", nullable = false)
	private UUID brandId;

	@Column(name = "environment_id", nullable = false)
	private UUID environmentId;

	@Column(name = "name", nullable = false)
	private String name;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "permission", nullable = false, columnDefinition = "jsonb")
	private JsonNode permission;
}
