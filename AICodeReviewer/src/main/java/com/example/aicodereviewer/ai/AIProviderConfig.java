package com.example.aicodereviewer.ai;

import java.util.Map;

public interface AIProviderConfig {
    String getApiKey();
    int getTimeoutSeconds();
    int getMaxRetries();
    String getBaseUrl();
    Map<String, Object> getAdditionalConfig();
} 