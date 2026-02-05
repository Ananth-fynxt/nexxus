package fynxt.brand.fee.entity;

import fynxt.brand.fee.enums.ChargeFeeType;
import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee")
@Builder
public class Fee extends AuditingEntity {

	@EmbeddedId
	private EmbeddableFeeId feeId;

	@Column(name = "name")
	private String name;

	@Column(name = "currency")
	private String currency;

	@Column(name = "countries", columnDefinition = "TEXT[]")
	private String[] countries;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.fee.enums.ChargeFeeType"))
	@Enumerated(EnumType.STRING)
	@Column(name = "charge_fee_type", columnDefinition = "charge_fee_type")
	private ChargeFeeType chargeFeeType;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "flow_action_id")
	private String flowActionId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;
}
