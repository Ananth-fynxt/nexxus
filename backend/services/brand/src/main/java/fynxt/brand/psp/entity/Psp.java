package fynxt.brand.psp.entity;

import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psps")
@Audited
@EqualsAndHashCode(callSuper = true)
public class Psp extends AuditingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "logo")
	private String logo;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "credential", nullable = false, columnDefinition = "jsonb")
	private JsonNode credential;

	@Column(name = "timeout")
	private Integer timeout;

	@Column(name = "block_vpn_access")
	private Boolean blockVpnAccess;

	@Column(name = "block_data_center_access")
	private Boolean blockDataCenterAccess;

	@Column(name = "failure_rate")
	private Boolean failureRate;

	@Column(name = "ip_address", columnDefinition = "TEXT[]")
	private String[] ipAddress;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "flow_target_id")
	private String flowTargetId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;

	@Column(name = "failure_rate_threshold")
	private Float failureRateThreshold;

	@Column(name = "failure_rate_duration_minutes")
	private Integer failureRateDurationMinutes;
}
