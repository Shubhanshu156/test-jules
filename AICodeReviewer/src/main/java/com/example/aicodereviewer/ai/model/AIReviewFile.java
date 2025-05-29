package com.example.aicodereviewer.ai.model;

import java.util.Objects;

public class AIReviewFile {
    private String filePath;
    private String content; // Optional
    private String diff;    // Optional
    private String language; // Optional

    // Constructors
    public AIReviewFile(String filePath, String content, String language) {
        this.filePath = filePath;
        this.content = content;
        this.language = language;
    }

    public AIReviewFile(String filePath) {
        this.filePath = filePath;
    }


    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // hashCode, equals, toString for potential use in collections/logging
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIReviewFile that = (AIReviewFile) o;
        return Objects.equals(filePath, that.filePath) &&
               Objects.equals(content, that.content) &&
               Objects.equals(diff, that.diff) &&
               Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, content, diff, language);
    }

    @Override
    public String toString() {
        return "AIReviewFile{" +
               "filePath='" + filePath + '\'' +
               ", language='" + language + '\'' +
               // Avoid printing full content/diff in logs by default
               (content != null ? ", content.length=" + content.length() : "") +
               (diff != null ? ", diff.length=" + diff.length() : "") +
               '}';
    }
}
