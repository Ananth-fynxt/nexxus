package fynxt.brand.transaction.repository.specifications;

import fynxt.brand.transaction.entity.Transaction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.regex.Pattern;

import org.springframework.data.jpa.domain.Specification;

public final class TransactionSpecifications {

	private TransactionSpecifications() {}

	private static final Pattern VALID_FIELD_NAME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

	public static Specification<Transaction> fieldEquals(String fieldName, Object value) {
		validateFieldName(fieldName);
		if (value == null) {
			throw new IllegalArgumentException("Filter value cannot be null");
		}
		return (root, query, builder) -> builder.equal(root.get(fieldName), value);
	}

	public static Specification<Transaction> fieldIn(String fieldName, Collection<?> values) {
		validateFieldName(fieldName);
		if (values == null || values.isEmpty()) {
			throw new IllegalArgumentException("Filter values cannot be null or empty");
		}
		return (root, query, builder) -> root.get(fieldName).in(values);
	}

	private static void validateFieldName(String fieldName) {
		if (fieldName == null || fieldName.isBlank()) {
			throw new IllegalArgumentException("Field name cannot be null or blank");
		}
		if (!VALID_FIELD_NAME.matcher(fieldName).matches()) {
			throw new IllegalArgumentException("Invalid field name: " + fieldName);
		}
	}

	public static Specification<Transaction> createdBetween(LocalDateTime from, LocalDateTime to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Date range parameters cannot be null");
		}
		return (root, query, builder) -> builder.between(root.get("createdAt"), from, to);
	}

	public static Specification<Transaction> latestVersion() {
		return (root, query, builder) -> {
			if (query != null) {
				query.distinct(true);
			}
			var subquery = query != null
					? query.subquery(Integer.class)
					: builder.createQuery().subquery(Integer.class);
			var subRoot = subquery.from(Transaction.class);
			subquery.select(builder.max(subRoot.get("id").get("version")))
					.where(builder.equal(
							subRoot.get("id").get("txnId"), root.get("id").get("txnId")));
			return builder.equal(root.get("id").get("version"), subquery);
		};
	}
}
