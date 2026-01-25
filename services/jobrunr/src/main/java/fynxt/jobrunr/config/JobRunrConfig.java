package fynxt.jobrunr.config;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunrConfig {

	@Bean
	public StorageProvider storageProvider(DataSource dataSource) {
		return SqlStorageProviderFactory.using(dataSource);
	}

	@Bean
	@ConditionalOnProperty(name = "jobrunr.background-job-server.enabled", havingValue = "true", matchIfMissing = false)
	public JobRunrConfigurationResult jobRunrConfiguration(
			StorageProvider storageProvider, ApplicationContext applicationContext) {

		return JobRunr.configure()
				.useJsonMapper(new JacksonJsonMapper())
				.useStorageProvider(storageProvider)
				.useJobActivator(applicationContext::getBean)
				.useBackgroundJobServer()
				.useDashboard()
				.initialize();
	}
}
