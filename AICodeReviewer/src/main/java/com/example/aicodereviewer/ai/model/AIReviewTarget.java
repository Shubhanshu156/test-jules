package com.example.aicodereviewer.ai.model;

import java.util.List;
import java.util.Objects;

public class AIReviewTarget {
    private AnalyzeTargetType type;
    private List<AIReviewFile> files;
    private String commitSha; // Optional

    // Constructors
    public AIReviewTarget(AnalyzeTargetType type, List<AIReviewFile> files) {
        this.type = type;
        this.files = files;
    }

    public AIReviewTarget(AnalyzeTargetType type, List<AIReviewFile> files, String commitSha) {
        this.type = type;
        this.files = files;
        this.commitSha = commitSha;
    }

    // Getters and Setters
    public AnalyzeTargetType getType() {
        return type;
    }

    public void setType(AnalyzeTargetType type) {
        this.type = type;
    }

    public List<AIReviewFile> getFiles() {
        return files;
    }

    public void setFiles(List<AIReviewFile> files) {
        this.files = files;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    // hashCode, equals, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewTarget that = (AIReviewTarget) o;
        return type == that.type &&
               Objects.equals(files, that.files) &&
               Objects.equals(commitSha, that.commitSha);
    }

    @Override
    public int hashCode() {
        // For caching, content of files is more important than the list object itself
        int filesHashCode = 0;
        if (files != null) {
            for (AIReviewFile file : files) {
                filesHashCode = 31 * filesHashCode + (file != null ? file.hashCode() : 0);
            }
        }
        return Objects.hash(type, filesHashCode, commitSha);
    }

    @Override
    public String toString() {
        return "AIReviewTarget{" +
               "type=" + type +
               ", files.size=" + (files != null ? files.size() : "null") +
               ", commitSha='" + commitSha + '\'' +
               '}';
    }
}
