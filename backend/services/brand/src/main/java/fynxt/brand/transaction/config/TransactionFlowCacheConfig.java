package fynxt.brand.transaction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TransactionFlowCacheConfig {

	public static final String CACHE_NAME = "flowNextStatuses";

	@Bean("flowCacheManager")
	public CacheManager flowCacheManager(TransactionFlowConfigurationProperties properties) {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager(CACHE_NAME);
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.maximumSize(properties.getMaximumSize())
				.expireAfterWrite(properties.getExpireAfterWrite())
				.recordStats());
		return cacheManager;
	}
}
