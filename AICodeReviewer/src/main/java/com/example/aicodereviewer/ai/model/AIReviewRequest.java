package com.example.aicodereviewer.ai.model;

import java.util.Objects;

public class AIReviewRequest {
    private String requestId;
    private String timestamp; // ISO 8601 datetime string
    private AIReviewTarget analyzeTarget;
    private AIReviewPreferences reviewPreferences;

    // Constructors
    public AIReviewRequest(String requestId, String timestamp, AIReviewTarget analyzeTarget, AIReviewPreferences reviewPreferences) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.analyzeTarget = analyzeTarget;
        this.reviewPreferences = reviewPreferences;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public AIReviewTarget getAnalyzeTarget() {
        return analyzeTarget;
    }

    public void setAnalyzeTarget(AIReviewTarget analyzeTarget) {
        this.analyzeTarget = analyzeTarget;
    }

    public AIReviewPreferences getReviewPreferences() {
        return reviewPreferences;
    }

    public void setReviewPreferences(AIReviewPreferences reviewPreferences) {
        this.reviewPreferences = reviewPreferences;
    }

    // hashCode, equals, toString
    // Crucial for caching: defines what makes two requests "the same" for caching purposes.
    // For this mock, we'll primarily consider the analyzeTarget's content.
    // RequestId and timestamp should ideally not be part of this for caching equivalence.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewRequest that = (AIReviewRequest) o;
        return Objects.equals(analyzeTarget, that.analyzeTarget) &&
               Objects.equals(reviewPreferences, that.reviewPreferences); // Preferences might also affect results
    }

    @Override
    public int hashCode() {
        return Objects.hash(analyzeTarget, reviewPreferences);
    }

    @Override
    public String toString() {
        return "AIReviewRequest{" +
               "requestId='" + requestId + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", analyzeTarget=" + analyzeTarget +
               ", reviewPreferences=" + reviewPreferences +
               '}';
    }

    /**
     * Generates a cache key based on the content-defining parts of the request.
     * This is more explicit for caching than relying solely on hashCode() if
     * we want to exclude requestId and timestamp from the key.
     */
    public String getCacheKey() {
        // Using Objects.hash on the relevant fields.
        // For AIReviewTarget and AIReviewPreferences, their own hashCode methods should be well-defined.
        return String.valueOf(Objects.hash(analyzeTarget, reviewPreferences));
    }
}
