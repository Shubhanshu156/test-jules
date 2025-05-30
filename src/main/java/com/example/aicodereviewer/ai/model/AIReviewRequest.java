package com.example.aicodereviewer.ai.model;

import java.util.Map;

public class AIReviewRequest {

    private String codeContent;
    private Map<String, String> options; // e.g., language, severity threshold

    public AIReviewRequest(String codeContent, Map<String, String> options) {
        this.codeContent = codeContent;
        this.options = options;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
