package fynxt.jobrunr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jobrunr")
public record JobRunrProperties(JobScheduler jobScheduler, BackgroundJobServer backgroundJobServer) {

	public record JobScheduler(Boolean enabled) {}

	public record BackgroundJobServer(Boolean enabled) {}
}
