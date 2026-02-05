package fynxt.brand.pspgroup.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PspGroupPspId implements Serializable {

	private Integer pspGroupId;
	private Integer pspGroupVersion;
	private UUID pspId;
}
