package fynxt.brand.routingrule.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EmbeddableRoutingRuleId implements Serializable {

	@Column(name = "id")
	private Integer id;

	@Column(name = "version")
	private Integer version;
}
