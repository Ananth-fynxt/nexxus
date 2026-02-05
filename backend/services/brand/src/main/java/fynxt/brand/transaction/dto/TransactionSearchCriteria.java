package fynxt.brand.transaction.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class TransactionSearchCriteria {

	@Min(0) private Integer page;

	@Min(1) private Integer size;

	private String sortBy;

	@JsonProperty("sortDirection")
	@lombok.Getter(AccessLevel.NONE)
	private String sortDirectionString;

	private Map<String, Object> filters = new HashMap<>();

	@JsonIgnore
	public Sort.Direction getSortDirection() {
		if (sortDirectionString == null || sortDirectionString.isBlank()) {
			return null;
		}
		try {
			return Sort.Direction.valueOf(sortDirectionString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
