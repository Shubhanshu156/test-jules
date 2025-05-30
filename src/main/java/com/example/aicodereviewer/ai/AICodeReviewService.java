package com.example.aicodereviewer.ai;

// These imports will likely be needed once the request/response classes are created.
// For now, we'll use fully qualified names to avoid compilation errors if they don't exist yet.
// import com.example.aicodereviewer.ai.model.AIModelInfo;
// import com.example.aicodereviewer.ai.model.AIReviewRequest;
// import com.example.aicodereviewer.ai.model.AIReviewResponse;

public interface AICodeReviewService {

    /**
     * Analyzes the given code using an AI model.
     *
     * @param request The request containing the code to be analyzed and other parameters.
     * @return The response from the AI model, including suggestions and analysis.
     */
    com.example.aicodereviewer.ai.model.AIReviewResponse analyzeCode(com.example.aicodereviewer.ai.model.AIReviewRequest request);

    /**
     * Checks the health of the AI code review service.
     *
     * @return true if the service is healthy and operational, false otherwise.
     */
    boolean isHealthy();

    /**
     * Retrieves information about the AI model being used by the service.
     *
     * @return An object containing details about the AI model.
     */
    com.example.aicodereviewer.ai.model.AIModelInfo getModelInfo();
}
