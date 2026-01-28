package fynxt.database.spec;

import fynxt.database.audit.AuditingEntity;

import org.springframework.data.jpa.domain.Specification;

public final class SoftDeleteSpec {

	private SoftDeleteSpec() {}

	public static <T extends AuditingEntity> Specification<T> notDeleted() {
		return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
	}

	public static <T extends AuditingEntity> Specification<T> onlyDeleted() {
		return (root, query, cb) -> cb.isNotNull(root.get("deletedAt"));
	}

	public static <T extends AuditingEntity> Specification<T> includingDeleted() {
		return (root, query, cb) -> cb.conjunction();
	}
}
