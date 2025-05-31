package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Main service for AI code review. Supports multiple providers and provider-aware caching.
 */
public class AICodeReviewService {
    private static final Logger LOGGER = Logger.getLogger(AICodeReviewService.class.getName());
    private static final int MAX_CACHE_SIZE = 20;
    private final Map<String, AIReviewResponse> responseCache = new LinkedHashMap<String, AIReviewResponse>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, AIReviewResponse> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    private final AIProviderFactory providerFactory = new AIProviderFactory();
    private boolean useMock = true; // Feature flag for migration
    private final Object cacheLock = new Object();

    /**
     * Analyze code using the specified provider and config.
     */
    public AIReviewResponse analyzeCode(AIReviewRequest request, String providerType, AIProviderConfig config) throws AIProviderException {
        String cacheKey = getCacheKey(request, providerType, config);
        synchronized (cacheLock) {
            if (responseCache.containsKey(cacheKey)) {
                AIReviewResponse cached = responseCache.get(cacheKey);
                cached.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
                LOGGER.info("Cache hit for provider: " + providerType);
                return cached;
            }
        }
        AIProvider provider = providerFactory.createProvider(providerType, config);
        AIReviewResponse response = provider.analyzeCode(request);
        synchronized (cacheLock) {
            responseCache.put(cacheKey, response);
        }
        return response;
    }

    /**
     * Backward compatible method. Uses mock or real provider based on feature flag.
     */
    public AIReviewResponse analyzeCode(AIReviewRequest request) {
        if (useMock) {
            // Use legacy mock logic (could delegate to a MockAIProvider)
            return new MockAIService().analyzeCode(request);
        } else {
            // Default to Gemini with dummy config for now
            try {
                AIProviderConfig config = new DefaultAIProviderConfig.Builder().apiKey("demo-key").build();
                return analyzeCode(request, "gemini", config);
            } catch (Exception e) {
                LOGGER.severe("AI provider error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void setUseMock(boolean useMock) {
        this.useMock = useMock;
    }

    private String getCacheKey(AIReviewRequest request, String providerType, AIProviderConfig config) {
        return providerType + ":" + request.getCacheKey() + ":" + Objects.hashCode(config.getApiKey());
    }
} 