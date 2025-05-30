package com.example.aicodereviewer.ai.model;

import java.util.List;
import java.util.ArrayList; // Added for initializing suggestions

public class AIReviewResponse {

    private List<AIReviewIssue> issues; // Changed from suggestions (List<String>) to issues (List<AIReviewIssue>)
    private boolean success;
    private String errorMesage;

    // Default constructor
    public AIReviewResponse() {
        this.issues = new ArrayList<>(); // Initialize to avoid null pointer
    }

    public AIReviewResponse(List<AIReviewIssue> issues, boolean success, String errorMessage) {
        this.issues = issues;
        this.success = success;
        this.errorMesage = errorMessage;
    }

    public List<AIReviewIssue> getIssues() { // Changed from getSuggestions
        return issues;
    }

    public void setIssues(List<AIReviewIssue> issues) { // Changed from setSuggestions
        this.issues = issues;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMesage() {
        return errorMesage;
    }

    public void setErrorMesage(String errorMesage) {
        this.errorMesage = errorMesage;
    }
}
