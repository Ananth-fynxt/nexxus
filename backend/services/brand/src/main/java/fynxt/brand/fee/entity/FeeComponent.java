package fynxt.brand.fee.entity;

import fynxt.brand.fee.enums.FeeComponentType;
import fynxt.database.hibernate.PostgreSQLEnumType;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee_components")
public class FeeComponent {

	@EmbeddedId
	private EmbeddableFeeComponentId feeComponentId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters =
					@org.hibernate.annotations.Parameter(
							name = "enumClass",
							value = "fynxt.brand.fee.enums.FeeComponentType"))
	@Enumerated(EnumType.STRING)
	@Column(name = "fee_component_type", columnDefinition = "fee_component_type")
	private FeeComponentType type;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "min_value")
	private BigDecimal minValue;

	@Column(name = "max_value")
	private BigDecimal maxValue;
}
