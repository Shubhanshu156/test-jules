package com.example.aicodereviewer.ai;

import java.util.HashMap;
import java.util.Map;

public class DefaultAIProviderConfig implements AIProviderConfig {
    private final String apiKey;
    private final int timeoutSeconds;
    private final int maxRetries;
    private final String baseUrl;
    private final Map<String, Object> additionalConfig;

    private DefaultAIProviderConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.timeoutSeconds = builder.timeoutSeconds;
        this.maxRetries = builder.maxRetries;
        this.baseUrl = builder.baseUrl;
        this.additionalConfig = builder.additionalConfig;
    }

    @Override
    public String getApiKey() { return apiKey; }
    @Override
    public int getTimeoutSeconds() { return timeoutSeconds; }
    @Override
    public int getMaxRetries() { return maxRetries; }
    @Override
    public String getBaseUrl() { return baseUrl; }
    @Override
    public Map<String, Object> getAdditionalConfig() { return additionalConfig; }

    public static class Builder {
        private String apiKey;
        private int timeoutSeconds = 30;
        private int maxRetries = 3;
        private String baseUrl;
        private Map<String, Object> additionalConfig = new HashMap<>();

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder timeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; return this; }
        public Builder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder additionalConfig(String key, Object value) { this.additionalConfig.put(key, value); return this; }
        public DefaultAIProviderConfig build() { return new DefaultAIProviderConfig(this); }
    }
} 