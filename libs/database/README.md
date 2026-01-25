# Database

JPA/Hibernate utilities with auditing, soft delete, and PostgreSQL enum support.

## Usage

### 1. Add dependency

In your service's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":libs:database"))
}
```

### 2. Configure datasource

Set the following properties in your `application.yml` file:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb # Database URL (required)
    username: user # Database username (required)
    password: pass # Database password (required)

  jpa:
    hibernate:
      ddl-auto: validate # Schema management (optional) - defaults to none
    show-sql: false # Show SQL queries (optional) - defaults to false
    properties:
      hibernate:
        format_sql: true # Format SQL output (optional) - defaults to false
```

## Features

### Auditing Entity

Extend `AuditingEntity` to automatically track creation, modification, and deletion:

```java
@Entity
public class User extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

Provides these fields automatically:
- `createdAt` / `createdBy` - set on insert
- `updatedAt` / `updatedBy` - set on update
- `deletedAt` / `deletedBy` - for soft delete

### Soft Delete

Use the entity methods:

```java
user.softDelete(userId);  // Mark as deleted
user.restore();           // Restore deleted entity
user.isDeleted();         // Check if deleted
```

Use specifications for queries:

```java
// Find only non-deleted
userRepository.findAll(SoftDeleteSpec.notDeleted());

// Find only deleted
userRepository.findAll(SoftDeleteSpec.onlyDeleted());

// Find all including deleted
userRepository.findAll(SoftDeleteSpec.includingDeleted());
```

### PostgreSQL Enum Type

Map Java enums to PostgreSQL enum types:

```java
@Entity
public class Order extends AuditingEntity {

    @Type(PostgreSQLEnumType.class)
    @Column(columnDefinition = "order_status")
    private OrderStatus status;
}
```

### JsonNode Converter

Store JSON data in text columns (auto-applied):

```java
@Entity
public class Config extends AuditingEntity {

    private JsonNode metadata; // Automatically converts to/from String
}
```

### Custom Auditor

Override the default auditor to provide actual user context:

```java
@Component("auditorProvider")
@Primary
public class SecurityAuditorProvider implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(auth -> Integer.parseInt(auth.getName()));
    }
}
```

## Minimum Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: pass
```
