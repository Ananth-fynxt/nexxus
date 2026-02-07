package fynxt.brand.psp.entity;

import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "maintenance_windows")
@Audited
public class MaintenanceWindow extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "psp_id")
	private UUID pspId;

	@Column(name = "flow_action_id")
	private String flowActionId;

	@Column(name = "start_at")
	private LocalDateTime startAt;

	@Column(name = "end_at")
	private LocalDateTime endAt;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;
}
