package com.example.aicodereviewer.ai;

// These imports will likely be needed once the request/response classes are created.
// For now, we'll use fully qualified names to avoid compilation errors if they don't exist yet.
// import com.example.aicodereviewer.ai.model.AIModelInfo;
// import com.example.aicodereviewer.ai.model.AIReviewRequest;
// import com.example.aicodereviewer.ai.model.AIReviewResponse;

public interface AiCodeReviewService {
    /**
     * Analyzes code and returns review issues
     * @param request The code review request
     * @return AI review response
     * @throws AIProviderException if analysis fails
     */
    AIReviewResponse analyzeCode(AIReviewRequest request) throws AIProviderException;

    /**
     * Gets the provider name (e.g., "gemini", "chatgpt", "deepseek")
     */
    String getProviderName();

    /**
     * Checks if the provider is available/configured
     */
    boolean isAvailable();

    /**
     * Gets provider-specific configuration requirements
     */
    Set<String> getRequiredConfigKeys();
}