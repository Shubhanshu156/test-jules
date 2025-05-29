package com.example.aicodereviewer.ai.model;

import java.util.Objects;

public class AIReviewSummary {
    private int criticalIssues;
    private int warningIssues;
    private int suggestionIssues;
    private int infoIssues; // Added for completeness, though not in original JSON sketch
    private int totalFilesAnalyzed;
    private long analysisDurationMs;

    // Constructor
    public AIReviewSummary() {
        this.criticalIssues = 0;
        this.warningIssues = 0;
        this.suggestionIssues = 0;
        this.infoIssues = 0;
        this.totalFilesAnalyzed = 0;
        this.analysisDurationMs = 0;
    }

    // Getters and Setters
    public int getCriticalIssues() {
        return criticalIssues;
    }

    public void setCriticalIssues(int criticalIssues) {
        this.criticalIssues = criticalIssues;
    }

    public int getWarningIssues() {
        return warningIssues;
    }

    public void setWarningIssues(int warningIssues) {
        this.warningIssues = warningIssues;
    }

    public int getSuggestionIssues() {
        return suggestionIssues;
    }

    public void setSuggestionIssues(int suggestionIssues) {
        this.suggestionIssues = suggestionIssues;
    }

    public int getInfoIssues() {
        return infoIssues;
    }

    public void setInfoIssues(int infoIssues) {
        this.infoIssues = infoIssues;
    }
    
    public int getTotalIssues() {
        return criticalIssues + warningIssues + suggestionIssues + infoIssues;
    }

    public int getTotalFilesAnalyzed() {
        return totalFilesAnalyzed;
    }

    public void setTotalFilesAnalyzed(int totalFilesAnalyzed) {
        this.totalFilesAnalyzed = totalFilesAnalyzed;
    }

    public long getAnalysisDurationMs() {
        return analysisDurationMs;
    }

    public void setAnalysisDurationMs(long analysisDurationMs) {
        this.analysisDurationMs = analysisDurationMs;
    }

    // Helper methods to increment counts
    public void incrementCritical() { this.criticalIssues++; }
    public void incrementWarning() { this.warningIssues++; }
    public void incrementSuggestion() { this.suggestionIssues++; }
    public void incrementInfo() { this.infoIssues++; }


    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewSummary that = (AIReviewSummary) o;
        return criticalIssues == that.criticalIssues &&
               warningIssues == that.warningIssues &&
               suggestionIssues == that.suggestionIssues &&
               infoIssues == that.infoIssues &&
               totalFilesAnalyzed == that.totalFilesAnalyzed &&
               analysisDurationMs == that.analysisDurationMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(criticalIssues, warningIssues, suggestionIssues, infoIssues, totalFilesAnalyzed, analysisDurationMs);
    }

    @Override
    public String toString() {
        return "AIReviewSummary{" +
               "criticalIssues=" + criticalIssues +
               ", warningIssues=" + warningIssues +
               ", suggestionIssues=" + suggestionIssues +
               ", infoIssues=" + infoIssues +
               ", totalFilesAnalyzed=" + totalFilesAnalyzed +
               ", analysisDurationMs=" + analysisDurationMs +
               '}';
    }
}
