package com.example.aicodereviewer.ai.model;

package com.example.aicodereviewer.ai.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AIReviewIssue {
    private final String id; // New field
    private String filePath;
    private int lineStart;
    private Integer lineEnd; // Optional
    private IssueType type;
    private IssueSeverity severity;
    private String message;
    private String explanation;   // Optional
    private String suggestedFix;  // Optional
    private List<String> reportedByModels;

    // Constructor
    public AIReviewIssue(String filePath, int lineStart, Integer lineEnd, IssueType type, IssueSeverity severity, String message, String explanation, String suggestedFix) {
        this.id = UUID.randomUUID().toString(); // Initialize ID
        this.filePath = filePath;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.explanation = explanation;
        this.suggestedFix = suggestedFix;
        this.reportedByModels = new ArrayList<>();
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

    public List<String> getReportedByModels() {
        // Defensive copy
        return reportedByModels != null ? new ArrayList<>(reportedByModels) : new ArrayList<>();
    }

    public void setReportedByModels(List<String> reportedByModels) {
        // Defensive copy
        this.reportedByModels = reportedByModels != null ? new ArrayList<>(reportedByModels) : new ArrayList<>();
    }

    public void addReportedByModel(String modelName) {
        if (this.reportedByModels == null) {
            this.reportedByModels = new ArrayList<>();
        }
        if (modelName != null && !modelName.trim().isEmpty() && !this.reportedByModels.contains(modelName)) {
            this.reportedByModels.add(modelName);
        }
    }

    public String getId() { // Getter for ID
        return id;
    }

    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewIssue that = (AIReviewIssue) o;
        // For uniqueness in aggregation, message, filePath, lineStart, type and severity are key.
        // Other fields like explanation, suggestedFix might differ slightly between models.
        // reportedByModels is intentionally excluded from equals/hashCode for finding unique issues.
        return lineStart == that.lineStart &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(message, that.message) &&
               type == that.type &&
               severity == that.severity;
    }

    @Override
    public int hashCode() {
        // Only include fields that define uniqueness for aggregation purposes
        return Objects.hash(filePath, lineStart, message, type, severity);
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
               ", explanation='" + explanation + '\'' +
               ", suggestedFix='" + suggestedFix + '\'' +
               ", reportedByModels=" + (reportedByModels != null ? reportedByModels.toString() : "[]") +
               ", id='" + id + '\'' + // Added ID to toString for debugging
               '}';
    }
}
