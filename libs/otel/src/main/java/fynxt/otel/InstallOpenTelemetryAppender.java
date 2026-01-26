package fynxt.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class InstallOpenTelemetryAppender {

	private final OpenTelemetry openTelemetry;

	InstallOpenTelemetryAppender(OpenTelemetry openTelemetry) {
		this.openTelemetry = openTelemetry;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void installAppender() {
		OpenTelemetryAppender.install(this.openTelemetry);
	}
}
