package fynxt.brand.brand.entity;

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
@Table(name = "brands")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "fi_id", nullable = true)
	private Short fiId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;
}
