package fynxt.brand.transaction.service;

import fynxt.brand.transaction.config.TransactionFlowCacheConfig;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.flowdefinition.entity.FlowDefinition;
import fynxt.flowdefinition.repository.FlowDefinitionRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionFlowConfigurationService {

	@Autowired
	private FlowDefinitionRepository flowDefinitionRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private Cache<String, List<TransactionStatus>> nextStatusCache;

	@PostConstruct
	public void initializeCache() {
		this.nextStatusCache = Caffeine.newBuilder()
				.maximumSize(TransactionFlowCacheConfig.FLOW_CACHE_MAXIMUM_SIZE)
				.expireAfterWrite(TransactionFlowCacheConfig.FLOW_CACHE_EXPIRE_AFTER_WRITE)
				.recordStats()
				.build();
	}

	private String generateCacheKey(String flowTargetId, String flowActionId, TransactionStatus currentStatus) {
		return flowTargetId + "_" + flowActionId + "_" + currentStatus.name();
	}

	public List<TransactionStatus> getNextStatuses(
			String flowTargetId, String flowActionId, TransactionStatus currentStatus) {
		String cacheKey = generateCacheKey(flowTargetId, flowActionId, currentStatus);

		return nextStatusCache.get(cacheKey, key -> {
			Optional<FlowDefinition> flowDefinition =
					flowDefinitionRepository.findByFlowTargetIdAndFlowActionId(flowTargetId, flowActionId);

			if (flowDefinition.isEmpty() || flowDefinition.get().getFlowConfiguration() == null) {
				return Collections.emptyList();
			}

			try {
				JsonNode flowConfig = flowDefinition.get().getFlowConfiguration();

				if (flowConfig != null && flowConfig.isTextual()) {
					String configString = flowConfig.asText();
					flowConfig = objectMapper.readTree(configString);
				}

				if (flowConfig == null || !flowConfig.isObject()) {
					return Collections.emptyList();
				}

				JsonNode currentStatusNode = flowConfig.get(currentStatus.name());

				if (currentStatusNode == null || !currentStatusNode.isArray()) {
					return Collections.emptyList();
				}

				return objectMapper.readValue(
						currentStatusNode.toString(), new TypeReference<List<TransactionStatus>>() {});
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse next statuses from JSONB", e);
			}
		});
	}

	public boolean isValidTransition(
			String flowTargetId, String flowActionId, TransactionStatus currentStatus, TransactionStatus nextStatus) {
		List<TransactionStatus> allowedTransitions = getNextStatuses(flowTargetId, flowActionId, currentStatus);
		return allowedTransitions.contains(nextStatus);
	}

	public Optional<FlowDefinition> getFlowDefinition(String flowTargetId, String flowActionId) {
		return flowDefinitionRepository.findByFlowTargetIdAndFlowActionId(flowTargetId, flowActionId);
	}

	public List<FlowDefinition> getAllFlowDefinitions() {
		return flowDefinitionRepository.findAll();
	}

	public void reloadFlowDefinition(String flowTargetId, String flowActionId) {
		nextStatusCache.asMap().keySet().removeIf(key -> key.startsWith(flowTargetId + "_" + flowActionId + "_"));
	}

	public void reloadAllFlowConfigurations() {
		nextStatusCache.invalidateAll();
	}

	public Map<String, Object> getCacheStats() {
		Map<String, Object> stats = new HashMap<>();
		stats.put("cacheSize", nextStatusCache.estimatedSize());
		stats.put("hitRate", nextStatusCache.stats().hitRate());
		stats.put("missRate", nextStatusCache.stats().missRate());
		stats.put("hitCount", nextStatusCache.stats().hitCount());
		stats.put("missCount", nextStatusCache.stats().missCount());
		stats.put("evictionCount", nextStatusCache.stats().evictionCount());
		stats.put("cachedKeys", nextStatusCache.asMap().keySet());
		return stats;
	}

	public void invalidateCacheEntry(String flowTargetId, String flowActionId) {
		nextStatusCache.asMap().keySet().removeIf(key -> key.startsWith(flowTargetId + "_" + flowActionId + "_"));
	}

	public void invalidateAllCache() {
		nextStatusCache.invalidateAll();
	}
}
