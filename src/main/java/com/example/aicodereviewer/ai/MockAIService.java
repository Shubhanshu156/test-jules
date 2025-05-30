package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIModelInfo;
import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.example.aicodereviewer.ai.model.AIReviewIssue; // Added import
import com.example.aicodereviewer.ai.model.IssueType;     // Added import

import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Required for AIModelInfo, though capabilities is null

public class MockAIService implements AICodeReviewService {

    private static final int MAX_LINE_LENGTH = 120;
    private static MockAIService instance;

    private MockAIService() {
        // Private constructor for singleton
    }

    public static MockAIService getInstance() {
        if (instance == null) {
            instance = new MockAIService();
        }
        return instance;
    }

    @Override
    public AIReviewResponse analyzeCode(AIReviewRequest request) {
        AIReviewResponse response = new AIReviewResponse(); // AIReviewResponse now initializes its 'issues' list
        List<AIReviewIssue> issues = new ArrayList<>();
        boolean success = true;
        String errorMessage = null;

        if (request == null || request.getCodeContent() == null || request.getCodeContent().isEmpty()) {
            issues.add(new AIReviewIssue("No code provided for analysis.", IssueType.ERROR, null, 0));
            success = false;
            errorMessage = "Request or code content is null/empty.";
            response.setIssues(issues);
            response.setSuccess(success);
            response.setErrorMesage(errorMessage);
            return response;
        }

        String code = request.getCodeContent();
        String[] lines = code.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // Check for FIXME
            if (line.contains("FIXME")) {
                issues.add(new AIReviewIssue("Contains FIXME: '" + line.trim() + "'", IssueType.CODE_SMELL, request.getOptions() != null ? request.getOptions().get("filePath") : null, i + 1));
            }
            // Check for long lines
            if (line.length() > MAX_LINE_LENGTH) {
                issues.add(new AIReviewIssue("Exceeds maximum line length of " + MAX_LINE_LENGTH + " characters (found " + line.length() + ")", IssueType.STYLE, request.getOptions() != null ? request.getOptions().get("filePath") : null, i + 1));
            }
            // Add more rule checks here if necessary (e.g., Kotlin 'var' - though this requires language context)
        }

        if (issues.isEmpty() && success) { // only add this if no other issues were found and process was successful
            issues.add(new AIReviewIssue("Mock analysis: No specific issues found. Code looks good!", IssueType.INFO));
        }

        response.setIssues(issues);
        response.setSuccess(success);
        response.setErrorMesage(errorMessage);
        return response;
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public AIModelInfo getModelInfo() {
        // As per requirement: modelName="MockService", modelVersion="1.0", provider="Internal", capabilities=null
        return new AIModelInfo("MockService", "1.0", "Internal", null);
    }
}
