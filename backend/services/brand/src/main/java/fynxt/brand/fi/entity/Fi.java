package fynxt.brand.fi.entity;

import fynxt.brand.user.enums.UserStatus;
import fynxt.common.enums.Scope;
import fynxt.database.audit.AuditingEntity;
import fynxt.database.hibernate.PostgreSQLEnumType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "fi")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fi extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Short id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "user_id")
	private Integer userId;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.common.enums.Scope"))
	@Enumerated(EnumType.STRING)
	@Column(name = "scope", nullable = false, columnDefinition = "scope")
	@Builder.Default
	private Scope scope = Scope.FI;

	@Type(
			value = PostgreSQLEnumType.class,
			parameters = @Parameter(name = "enumClass", value = "fynxt.brand.user.enums.UserStatus"))
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "user_status")
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;
}
