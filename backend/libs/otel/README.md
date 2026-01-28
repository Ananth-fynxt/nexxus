# OpenTelemetry

OpenTelemetry integration library with automatic configuration for Spring Boot applications.

## Usage

### 1. Add dependency

In your service's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":libs:otel"))
}
```

### 2. Configure OpenTelemetry

The library automatically configures:
- OpenTelemetry instrumentation
- Logback appender integration
- DataSource observation with Micrometer

### 3. Optional Configuration

Configure OpenTelemetry endpoints in your `application.yml`:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # Set to 0.0-1.0 for sampling rate
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces  # OTLP endpoint
```

## Features

### Automatic Configuration

- **OpenTelemetry Appender**: Automatically installs OpenTelemetry appender to Logback for log correlation
- **DataSource Observation**: Configures Micrometer observation for database operations
- **Auto-configuration**: Enabled via Spring Boot's auto-configuration mechanism

### Components

- `OtelAutoConfig`: Main auto-configuration class that sets up OpenTelemetry beans
- `InstallOpenTelemetryAppender`: Installs the OpenTelemetry appender to Logback after application startup

## Minimum Configuration

No additional configuration required. The library will auto-configure OpenTelemetry when included as a dependency.
