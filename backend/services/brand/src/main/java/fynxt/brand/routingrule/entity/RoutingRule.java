package fynxt.brand.routingrule.entity;

import fynxt.brand.routingrule.enums.PspSelectionMode;
import fynxt.brand.routingrule.enums.RoutingDuration;
import fynxt.brand.routingrule.enums.RoutingType;
import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routing_rules")
public class RoutingRule extends AuditingEntity {

	@EmbeddedId
	private EmbeddableRoutingRuleId routingRuleId;

	@Column(name = "name")
	private String name;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Enumerated(EnumType.STRING)
	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.routingrule.enums.PspSelectionMode"))
	@Column(name = "psp_selection_mode", columnDefinition = "psp_selection_mode")
	private PspSelectionMode pspSelectionMode;

	@Enumerated(EnumType.STRING)
	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.routingrule.enums.RoutingType"))
	@Column(name = "routing_type", columnDefinition = "routing_type")
	private RoutingType routingType;

	@Enumerated(EnumType.STRING)
	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.routingrule.enums.RoutingDuration"))
	@Column(name = "duration", columnDefinition = "routing_duration")
	private RoutingDuration duration;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "condition_json", columnDefinition = "jsonb")
	private JsonNode conditionJson;

	@Enumerated(EnumType.STRING)
	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Column(name = "status", columnDefinition = "status")
	private Status status;
}
