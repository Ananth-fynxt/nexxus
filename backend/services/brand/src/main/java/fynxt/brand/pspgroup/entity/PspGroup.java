package fynxt.brand.pspgroup.entity;

import fynxt.common.enums.Status;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psp_groups")
@Builder
public class PspGroup extends AuditingEntity {

	@EmbeddedId
	private EmbeddablePspGroupId pspGroupId;

	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name = "environment_id")
	private UUID environmentId;

	@Column(name = "name")
	private String name;

	@Column(name = "flow_action_id")
	private String flowActionId;

	@Column(name = "currency")
	private String currency;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;

	@OneToMany(mappedBy = "pspGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<PspGroupPsp> pspGroupPsps = new ArrayList<>();
}
