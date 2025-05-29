package com.example.aicodereviewer.ai.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AIReviewPreferences {
    private List<String> ruleSetIds;         // Optional
    private Integer maxIssuesPerFile;        // Optional
    private List<String> excludedPaths;      // Optional
    private Set<IssueType> enabledChecks;    // Added: Specific checks to run

    // Constructors
    public AIReviewPreferences() {}

    public AIReviewPreferences(List<String> ruleSetIds, Integer maxIssuesPerFile, List<String> excludedPaths, Set<IssueType> enabledChecks) {
        this.ruleSetIds = ruleSetIds;
        this.maxIssuesPerFile = maxIssuesPerFile;
        this.excludedPaths = excludedPaths;
        this.enabledChecks = enabledChecks;
    }

    // Getters and Setters
    public List<String> getRuleSetIds() {
        return ruleSetIds;
    }

    public void setRuleSetIds(List<String> ruleSetIds) {
        this.ruleSetIds = ruleSetIds;
    }

    public Integer getMaxIssuesPerFile() {
        return maxIssuesPerFile;
    }

    public void setMaxIssuesPerFile(Integer maxIssuesPerFile) {
        this.maxIssuesPerFile = maxIssuesPerFile;
    }

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    public Set<IssueType> getEnabledChecks() {
        return enabledChecks;
    }

    public void setEnabledChecks(Set<IssueType> enabledChecks) {
        this.enabledChecks = enabledChecks;
    }

    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewPreferences that = (AIReviewPreferences) o;
        return Objects.equals(ruleSetIds, that.ruleSetIds) &&
               Objects.equals(maxIssuesPerFile, that.maxIssuesPerFile) &&
               Objects.equals(excludedPaths, that.excludedPaths) &&
               Objects.equals(enabledChecks, that.enabledChecks); // Added to equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleSetIds, maxIssuesPerFile, excludedPaths, enabledChecks); // Added to hash
    }

    @Override
    public String toString() {
        return "AIReviewPreferences{" +
               "ruleSetIds=" + ruleSetIds +
               ", maxIssuesPerFile=" + maxIssuesPerFile +
               ", excludedPaths=" + excludedPaths +
               ", enabledChecks=" + enabledChecks + // Added to toString
               '}';
    }
}
