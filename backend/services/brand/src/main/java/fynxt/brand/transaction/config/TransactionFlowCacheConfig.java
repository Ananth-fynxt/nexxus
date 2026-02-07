package fynxt.brand.transaction.config;

import java.time.Duration;

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
	public static final int FLOW_CACHE_MAXIMUM_SIZE = 5000;
	public static final Duration FLOW_CACHE_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);

	@Bean("flowCacheManager")
	public CacheManager flowCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager(CACHE_NAME);
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.maximumSize(FLOW_CACHE_MAXIMUM_SIZE)
				.expireAfterWrite(FLOW_CACHE_EXPIRE_AFTER_WRITE)
				.recordStats());
		return cacheManager;
	}
}
