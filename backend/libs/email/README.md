# Email Library

A reusable library for sending templated emails using Azure Communication Services.

## Features

- Templated email sending with HTML and plain text support
- Azure Communication Services integration
- Asynchronous email execution
- Template service with extensible template providers
- Conditional auto-configuration (enabled via properties)
- Customizable through Spring beans

## Usage

### 1. Add Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":libs:email"))
}
```

### 2. Enable in Application Properties

```yaml
# application.yml
email:
  enabled: true                                    # Enable email library (required)
  connection-string: "endpoint=...;accesskey=..."  # Azure Communication Services connection string (required)
  sender-address: "noreply@example.com"           # Default sender address (optional)
  thread-pool-size: 5                              # Thread pool size for async execution (default: 5)
```

### 3. Inject and Use

```java
@Service
public class UserService {

    private final EmailService emailService;

    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendWelcomeEmail(String userEmail, String password) {
        EmailRequest request = EmailRequest.builder()
            .recipients(List.of(userEmail))
            .templateId("welcome-email")
            .templateData(Map.of(
                "userEmail", userEmail,
                "password", password,
                "loginUrl", "https://app.example.com/login",
                "companyName", "My Company"
            ))
            .description("Welcome email for new user")
            .build();

        EmailResponse response = emailService.sendTemplatedEmail(request);
        
        if (response.getIsSuccess() != null && response.getIsSuccess()) {
            // Email sent successfully
        }
    }
}
```

## Components

| Component | Description |
|-----------|-------------|
| `EmailService` | Interface for sending templated emails |
| `EmailServiceImpl` | Default implementation using Azure Communication Services |
| `EmailTemplateService` | Interface for template generation |
| `EmailTemplateServiceImpl` | Template service router that delegates to registered template providers |
| `WelcomeEmailTemplate` | Built-in welcome email template provider |
| `EmailRequest` | Request DTO with recipients, template ID, and template data |
| `EmailResponse` | Response DTO with execution status, message ID, and timing |
| `EmailExecutionStatus` | Enum: PENDING, IN_PROGRESS, SENT, FAILED |

## Creating Custom Templates

Implement `EmailTemplateService` to create custom email templates:

```java
@Component
public class PasswordResetTemplate implements EmailTemplateService {

    @Override
    public EmailTemplateContent generateTemplate(String templateId, Map<String, Object> templateData) {
        if (!"password-reset".equals(templateId)) {
            throw new IllegalArgumentException("Only 'password-reset' template ID is supported");
        }

        String resetUrl = (String) templateData.getOrDefault("resetUrl", "");
        String userName = (String) templateData.getOrDefault("userName", "User");

        String subject = "Password Reset Request";
        String htmlContent = buildHtmlContent(resetUrl, userName);
        String plainTextContent = buildPlainTextContent(resetUrl, userName);

        return EmailTemplateContent.builder()
            .subject(subject)
            .htmlContent(htmlContent)
            .plainTextContent(plainTextContent)
            .build();
    }

    private String buildHtmlContent(String resetUrl, String userName) {
        // Build HTML content
        return "<html>...</html>";
    }

    private String buildPlainTextContent(String resetUrl, String userName) {
        // Build plain text content
        return "Plain text content...";
    }
}
```

The `EmailTemplateServiceImpl` will automatically discover and use your custom template when the matching `templateId` is provided.

## Customization

Override any bean by defining your own:

```java
@Configuration
public class CustomEmailConfig {

    @Bean
    public EmailAsyncClient emailAsyncClient(EmailProperties properties) {
        // Custom Azure Communication Services client configuration
        return new EmailClientBuilder()
            .connectionString(properties.connectionString())
            .buildAsyncClient();
    }

    @Bean
    public ExecutorService emailExecutorService(EmailProperties properties) {
        // Custom thread pool configuration
        return Executors.newFixedThreadPool(properties.threadPoolSize());
    }
}
```

## Minimum Configuration

```yaml
email:
  enabled: true
  connection-string: "endpoint=...;accesskey=..."
```
