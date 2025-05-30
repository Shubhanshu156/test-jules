package com.example.aicodereviewer.ai.model;

public class AIReviewIssue {
    private String description;
    private IssueType type;
    private String filePath; // Optional: path to the file containing the issue
    private int lineNumber = -1; // Optional: line number of the issue, -1 if not applicable

    public AIReviewIssue(String description, IssueType type) {
        this.description = description;
        this.type = type;
    }

    public AIReviewIssue(String description, IssueType type, String filePath, int lineNumber) {
        this.description = description;
        this.type = type;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "AIReviewIssue{" +
                "description='" + description + '\'' +
                ", type=" + type +
                ", filePath='" + filePath + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
