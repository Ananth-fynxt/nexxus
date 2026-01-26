# Permission Library

A reusable library for permission and scope-based access control using Spring AOP.

## Features

- Permission-based access control with `@RequiresPermission` annotation
- Scope-based access control with `@RequiresScope` annotation
- Aspect-oriented programming for declarative security
- Configurable permission context holder
- Conditional auto-configuration (enabled via properties)

## Usage

### 1. Add Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":libs:permission"))
}
```

### 2. Enable in Application Properties

```yaml
# application.yml
permission:
  enabled: true  # Enable permission library (default: true)
```

### 3. Implement PermissionContextHolder

The library requires an implementation of `PermissionContextHolder` to be provided by your application:

```java
@Component
public class BrandEnvironmentContextHolder implements PermissionContextHolder {

    @Override
    public String getScope() {
        // Return the current user's scope (e.g., "FI", "EXTERNAL", "BRAND")
        // This could come from a security context, JWT token, etc.
        return getCurrentUserScope();
    }

    @Override
    public Map<String, Object> getRolePermissions() {
        // Return the role permissions map
        // Structure: Map<module, Map<"actions", List<action>>>
        // Example: {
        //   "users": {
        //     "actions": ["read", "write", "delete"]
        //   },
        //   "transactions": {
        //     "actions": ["read"]
        //   }
        // }
        return getCurrentUserRolePermissions();
    }
}
```

### 4. Use Annotations

#### @RequiresPermission

Use `@RequiresPermission` to protect methods or classes that require specific permissions:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    @RequiresPermission(module = "users", action = "read")
    public List<User> getUsers() {
        // Only users with "read" permission on "users" module can access this
        return userService.findAll();
    }

    @PostMapping
    @RequiresPermission(module = "users", action = "write")
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(module = "users", action = "delete")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

#### @RequiresScope

Use `@RequiresScope` to restrict access to specific scopes:

```java
@RestController
@RequestMapping("/api/admin")
@RequiresScope({"FI", "BRAND"})
public class AdminController {

    @GetMapping("/settings")
    public Settings getSettings() {
        // Only users with "FI" or "BRAND" scope can access this controller
        return settingsService.getSettings();
    }
}
```

You can also use it on individual methods:

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @GetMapping("/internal")
    @RequiresScope({"FI"})
    public List<Transaction> getInternalTransactions() {
        // Only "FI" scope users can access this
        return transactionService.findInternal();
    }

    @GetMapping("/external")
    @RequiresScope({"EXTERNAL"})
    public List<Transaction> getExternalTransactions() {
        // Only "EXTERNAL" scope users can access this
        return transactionService.findExternal();
    }
}
```

### 5. Custom Error Messages

Both annotations support custom error messages:

```java
@RequiresPermission(
    module = "users",
    action = "delete",
    errorMessage = "You are not authorized to delete users"
)
public void deleteUser(Long id) {
    // ...
}
```

## Components

| Component | Description |
|-----------|-------------|
| `PermissionService` | Interface for checking permissions |
| `PermissionServiceImpl` | Default implementation that validates permissions based on scope and role permissions |
| `PermissionContextHolder` | Interface for providing scope and role permissions (must be implemented by consuming application) |
| `PermissionCheckAspect` | AOP aspect that intercepts `@RequiresPermission` annotations |
| `ScopeCheckAspect` | AOP aspect that intercepts `@RequiresScope` annotations |
| `PermissionDeniedException` | Exception thrown when permission check fails |
| `@RequiresPermission` | Annotation for method/class-level permission checks |
| `@RequiresScope` | Annotation for method/class-level scope checks |

## Permission Logic

The library implements the following permission logic:

1. **Bypass Check**: If the request has `bypass.permission.check` attribute set to `true`, all checks are bypassed
2. **FI Scope**: Users with "FI" scope have all permissions (no validation needed)
3. **EXTERNAL Scope**: Users with "EXTERNAL" scope have all permissions (no validation needed)
4. **BRAND Scope**: Users with "BRAND" scope require role-based permission validation
   - The `PermissionContextHolder.getRolePermissions()` must return a map with the structure:
     ```java
     {
       "moduleName": {
         "actions": ["action1", "action2", ...]
       }
     }
     ```
   - The permission check validates that the required action exists in the actions list for the specified module

## Bypassing Permission Checks

For admin or internal service-to-service calls, you can bypass permission checks by setting a request attribute:

```java
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // Check for admin token or internal service token
                String token = request.getHeader("X-Internal-Token");
                if ("admin-secret-token".equals(token)) {
                    request.setAttribute("bypass.permission.check", true);
                }
                return true;
            }
        });
    }
}
```

## Customization

Override any bean by defining your own:

```java
@Configuration
public class CustomPermissionConfig {

    @Bean
    public PermissionService customPermissionService(PermissionContextHolder contextHolder) {
        // Custom permission service implementation
        return new CustomPermissionServiceImpl(contextHolder);
    }
}
```

## Minimum Configuration

```yaml
permission:
  enabled: true
```

Plus provide an implementation of `PermissionContextHolder` as a Spring bean.
