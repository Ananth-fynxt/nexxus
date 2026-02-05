package fynxt.jobrunr.config;

import javax.sql.DataSource;

import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration.class)
@ConditionalOnClass(StorageProvider.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnMissingBean(StorageProvider.class)
public class JobRunrStorageProviderAutoConfiguration {

	@Bean
	public StorageProvider jobRunrStorageProvider(DataSource dataSource) {
		return SqlStorageProviderFactory.using(dataSource);
	}
}
