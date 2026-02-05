package fynxt.brand.fee.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeePspId implements Serializable {
	private Integer feeId;
	private Integer feeVersion;
	private UUID pspId;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		FeePspId that = (FeePspId) obj;
		return Objects.equals(feeId, that.feeId)
				&& Objects.equals(feeVersion, that.feeVersion)
				&& Objects.equals(pspId, that.pspId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(feeId, feeVersion, pspId);
	}

	@Override
	public String toString() {
		return "FeePspId{" + "feeId='" + feeId + '\'' + ", feeVersion=" + feeVersion + ", pspId='" + pspId + '\'' + '}';
	}
}
