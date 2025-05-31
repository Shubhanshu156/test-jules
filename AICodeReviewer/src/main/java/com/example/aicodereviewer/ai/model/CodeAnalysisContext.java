package com.example.aicodereviewer.ai.model;

public class CodeAnalysisContext {

    private final String programmingLanguage;
    private final CodeComplexity codeComplexity;
    private final AnalysisType analysisType;

    public enum CodeComplexity {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum AnalysisType {
        SECURITY,
        PERFORMANCE,
        GENERAL_QUALITY,
        DOCUMENTATION,
        COMPLEX_ARCHITECTURE
    }

    public CodeAnalysisContext(String programmingLanguage, CodeComplexity codeComplexity, AnalysisType analysisType) {
        this.programmingLanguage = programmingLanguage;
        this.codeComplexity = codeComplexity;
        this.analysisType = analysisType;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public CodeComplexity getCodeComplexity() {
        return codeComplexity;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }
}
