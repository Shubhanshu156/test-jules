package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIModelInfo;
import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.example.aicodereviewer.ai.model.IssueType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenAIService implements AICodeReviewService {

    @Override
    public AIReviewResponse analyzeCode(AIReviewRequest request) {
        AIReviewResponse response = new AIReviewResponse();
        String filePath = null;
        if (request != null && request.getOptions() != null) {
            filePath = request.getOptions().get("filePath");
        }

        AIReviewIssue issue = new AIReviewIssue(
                "OpenAI analysis not yet implemented",
                IssueType.INFO,
                filePath, // Use extracted filePath
                0         // Placeholder line number
        );
        response.setIssues(Collections.singletonList(issue));
        response.setSuccess(true); // Indicate success despite being a placeholder
        return response;
    }

    @Override
    public boolean isHealthy() {
        return true; // For now, assume the service is healthy
    }

    @Override
    public AIModelInfo getModelInfo() {
        Map<String, Object> capabilities = new HashMap<>();
        // Example capabilities for a model like GPT-4
        capabilities.put("maxInputTokens", 128000); // GPT-4 Turbo context window
        capabilities.put("maxOutputTokens", 4096);
        capabilities.put("supportedModalities", Collections.singletonList("text")); // Can be expanded (e.g., "image" for vision models)
        capabilities.put("trainingDataCutoff", "April 2023"); // Example, actual value varies

        return new AIModelInfo(
                "GPT-4",        // modelName
                "gpt-4",        // modelVersion (often an ID used in API calls)
                "OpenAI",       // provider
                capabilities    // capabilities
        );
    }
}
