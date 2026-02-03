package fynxt.brand.brandrole.entity;

import fynxt.database.audit.AuditingEntity;
import fynxt.database.converter.JsonNodeConverter;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
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

	@Convert(converter = JsonNodeConverter.class)
	@Column(name = "permission", columnDefinition = "jsonb")
	private JsonNode permission;
}
