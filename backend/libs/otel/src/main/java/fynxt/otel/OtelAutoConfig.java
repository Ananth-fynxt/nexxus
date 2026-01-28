package fynxt.otel;

import javax.sql.DataSource;

import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.OpenTelemetry;
import net.ttddyy.observation.boot.autoconfigure.DataSourceNameResolver;
import net.ttddyy.observation.boot.autoconfigure.DataSourceObservationAutoConfiguration;
import net.ttddyy.observation.boot.autoconfigure.DefaultDataSourceNameResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = DataSourceObservationAutoConfiguration.class)
@ConditionalOnClass({DataSource.class, ObservationRegistry.class})
public class OtelAutoConfig {

	@Bean
	@ConditionalOnMissingBean
	public DataSourceNameResolver defaultDataSourceNameResolver() {
		return new DefaultDataSourceNameResolver();
	}

	@Bean
	InstallOpenTelemetryAppender installOpenTelemetryAppender(OpenTelemetry openTelemetry) {
		return new InstallOpenTelemetryAppender(openTelemetry);
	}
}
