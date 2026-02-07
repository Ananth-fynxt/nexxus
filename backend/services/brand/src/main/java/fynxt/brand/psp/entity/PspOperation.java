package fynxt.brand.psp.entity;

import fynxt.common.enums.Status;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psp_operations")
@IdClass(PspOperationId.class)
@Audited
public class PspOperation {

	@Id
	@Column(name = "psp_id")
	private UUID pspId;

	@Id
	@Column(name = "flow_action_id")
	private String flowActionId;

	@Id
	@Column(name = "flow_definition_id")
	private String flowDefinitionId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.common.enums.Status"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "status")
	@Builder.Default
	private Status status = Status.ENABLED;

	@Column(name = "currencies", columnDefinition = "text[]")
	private List<String> currencies;

	@Column(name = "countries", columnDefinition = "text[]")
	private List<String> countries;
}
