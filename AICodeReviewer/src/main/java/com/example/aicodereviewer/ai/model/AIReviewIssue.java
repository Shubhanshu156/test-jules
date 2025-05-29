package com.example.aicodereviewer.ai.model;

import java.util.Objects;

public class AIReviewIssue {
    private String filePath;
    private int lineStart;
    private Integer lineEnd; // Optional
    private IssueType type;
    private IssueSeverity severity;
    private String message;
    private String explanation;   // Optional
    private String suggestedFix;  // Optional

    // Constructor
    public AIReviewIssue(String filePath, int lineStart, Integer lineEnd, IssueType type, IssueSeverity severity, String message, String explanation, String suggestedFix) {
        this.filePath = filePath;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.explanation = explanation;
        this.suggestedFix = suggestedFix;
    }
    
    public AIReviewIssue(String filePath, int lineStart, IssueType type, IssueSeverity severity, String message) {
        this(filePath, lineStart, null, type, severity, message, null, null);
    }


    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }

    public Integer getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(Integer lineEnd) {
        this.lineEnd = lineEnd;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public IssueSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IssueSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public void setSuggestedFix(String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewIssue that = (AIReviewIssue) o;
        return lineStart == that.lineStart &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(lineEnd, that.lineEnd) &&
               type == that.type &&
               severity == that.severity &&
               Objects.equals(message, that.message) &&
               Objects.equals(explanation, that.explanation) &&
               Objects.equals(suggestedFix, that.suggestedFix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, lineStart, lineEnd, type, severity, message, explanation, suggestedFix);
    }

    @Override
    public String toString() {
        return "AIReviewIssue{" +
               "filePath='" + filePath + '\'' +
               ", lineStart=" + lineStart +
               ", lineEnd=" + lineEnd +
               ", type=" + type +
               ", severity=" + severity +
               ", message='" + message + '\'' +
               '}';
    }
}
