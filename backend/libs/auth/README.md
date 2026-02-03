# Fynxt Auth Library

A **pure authentication library** for Spring Boot applications. This library provides filters, strategies, and security configuration without any database dependencies. All business logic and database operations must be implemented by the consuming service.

## Pure Library Approach

This is a **pure library** that provides ONLY common authentication infrastructure:
- ✅ Authentication filters and strategies
- ✅ Security configuration (Spring Security setup)
- ✅ Route configuration (path-based auth rules)
- ✅ DTOs (LoginRequest, AuthResponse, UserInfo)
- ✅ Enums (TokenType, TokenStatus, AuthType, Scope)
- ✅ Context holders (BrandEnvironmentContext)
- ✅ Service interfaces (for implementations)
- ❌ **NO** service implementations (no business logic)
- ❌ **NO** database entities or repositories
- ❌ **NO** application-specific configuration
- ❌ **NO** token generation logic (service-specific)
- ❌ **NO** user authentication logic (service-specific)

## Features

- **Multiple Authentication Strategies**
  - JWT Bearer Token Authentication
  - Secret Token Authentication (X-SECRET-TOKEN header)
  - Admin Token Authentication (X-ADMIN-TOKEN header)

- **Security Features**
  - Token management with database persistence
  - Token revocation and expiration handling
  - Spring Security integration
  - Filter chain configuration
  - CORS configuration
  - Session management

- **Token Management**
  - Access token and refresh token support
  - Token hashing for secure storage
  - Automatic token cleanup
  - Token validation with database verification

## Architecture

### Filter Chain
The library implements a multi-layered filter chain:
1. **RawBodyCachingFilter** - Caches request body for reuse
2. **CorrelationIdWebFilter** - Adds correlation ID for request tracking
3. **AccessTokenOncePerRequestFilter** - Main authentication filter
4. **BrandEnvironmentContextFilter** - Sets brand/environment context

### Authentication Strategies
Strategies are ordered by priority using Spring's `@Order` annotation:
- **Order 0**: AdminTokenAuthenticationStrategy (X-ADMIN-TOKEN)
- **Order 1**: SecretTokenAuthenticationStrategy (X-SECRET-TOKEN)
- **Order 2**: JwtAuthenticationStrategy (Bearer token)

## Usage

### 1. Add Dependency

Add the auth library to your service's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":libs:auth"))
}
```

### 2. Configuration

Add the following properties to your `application.yml`:

```yaml
fynxt:
  auth:
    admin-token: ${ADMIN_TOKEN:your-admin-token}
  jwt:
    issuer: nexxus-app-prod
    audience: nexxus-api-prod
    signing-key-id: ${JWT_SIGNING_KEY_ID}
    refresh-signing-key-id: ${JWT_REFRESH_SIGNING_KEY_ID}
    access-token-expiration: PT1H  # 1 hour
    refresh-token-expiration: P7D  # 7 days

api:
  prefix: /api/v1
  frontend-url: http://localhost:3000

# Route configuration
route:
  public-paths:
    - /api/v1/auth/login
    - /api/v1/auth/token/refresh
  admin-token-paths:
    - /api/v1/admin/**
  secret-token-paths:
    - /api/v1/webhook/**
```

### 3. Enable Auto-Configuration

The library uses Spring Boot auto-configuration. Simply add the dependency and it will be automatically configured.

### 4. Implement Auth Controller

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(authService.logout(authorization));
    }
}
```

## Security Considerations

- Store JWT signing keys securely (use environment variables or secret management)
- Use HTTPS in production
- Implement rate limiting for authentication endpoints
- Regularly rotate signing keys
- Monitor for suspicious authentication patterns

## Database Schema

The library requires the following database tables:
- `tokens` - Stores access and refresh tokens with status tracking

See migration files in your service for schema details.
