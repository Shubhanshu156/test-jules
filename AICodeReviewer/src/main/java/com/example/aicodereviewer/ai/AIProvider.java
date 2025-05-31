package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import java.util.Set;

public interface AIProvider {
    AIReviewResponse analyzeCode(AIReviewRequest request) throws AIProviderException;
    String getProviderName();
    boolean isAvailable();
    Set<String> getRequiredConfigKeys();
} 