package fynxt.database.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class AuditingEntity {

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	private Integer createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", nullable = false)
	private Integer updatedBy;

	@Column(name = "deleted_by")
	private Integer deletedBy;

	public void softDelete(Integer deletedByUserId) {
		if (this.deletedAt == null) {
			this.deletedAt = LocalDateTime.now();
			this.deletedBy = deletedByUserId;
		}
	}

	public void softDelete() {
		softDelete(0);
	}

	public void restore() {
		this.deletedAt = null;
		this.deletedBy = null;
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}
}
