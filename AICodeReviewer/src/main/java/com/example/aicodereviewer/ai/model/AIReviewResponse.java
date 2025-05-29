package com.example.aicodereviewer.ai.model;

import java.util.List;
import java.util.Objects;

public class AIReviewResponse {
    private String responseId; // Echoed from request or new
    private String timestamp;  // ISO 8601 datetime string
    private AIReviewSummary summary;
    private List<AIReviewIssue> issues;

    // Constructor
    public AIReviewResponse(String responseId, String timestamp, AIReviewSummary summary, List<AIReviewIssue> issues) {
        this.responseId = responseId;
        this.timestamp = timestamp;
        this.summary = summary;
        this.issues = issues;
    }

    // Getters and Setters
    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public AIReviewSummary getSummary() {
        return summary;
    }

    public void setSummary(AIReviewSummary summary) {
        this.summary = summary;
    }

    public List<AIReviewIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<AIReviewIssue> issues) {
        this.issues = issues;
    }

    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewResponse that = (AIReviewResponse) o;
        // For responses, all fields might be important for equality check
        return Objects.equals(responseId, that.responseId) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(summary, that.summary) &&
               Objects.equals(issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseId, timestamp, summary, issues);
    }

    @Override
    public String toString() {
        return "AIReviewResponse{" +
               "responseId='" + responseId + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", summary=" + summary +
               ", issues.size=" + (issues != null ? issues.size() : "null") +
               '}';
    }
}
