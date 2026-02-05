package fynxt.jobrunr.config;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(JobRunr.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnMissingBean(JobScheduler.class)
@ConditionalOnProperty(name = "jobrunr.standalone", havingValue = "true")
@ConditionalOnProperty(name = "jobrunr.job-scheduler.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JobRunrProperties.class)
public class JobRunrAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(StorageProvider.class)
	public StorageProvider jobRunrStorageProvider(DataSource dataSource) {
		return SqlStorageProviderFactory.using(dataSource);
	}

	@Bean
	public JobRunrConfigurationResult jobRunrConfiguration(
			StorageProvider jobRunrStorageProvider,
			ApplicationContext applicationContext,
			JobRunrProperties properties) {

		var config = JobRunr.configure()
				.useJsonMapper(new JacksonJsonMapper())
				.useStorageProvider(jobRunrStorageProvider)
				.useJobActivator(applicationContext::getBean);

		if (properties.backgroundJobServer() != null
				&& Boolean.TRUE.equals(properties.backgroundJobServer().enabled())) {
			return config.useBackgroundJobServer().useDashboard().initialize();
		}
		return config.initialize();
	}

	@Bean
	public JobScheduler jobScheduler(JobRunrConfigurationResult jobRunrConfiguration) {
		return jobRunrConfiguration.getJobScheduler();
	}
}
