package com.example.aicodereviewer.ai.model;

public enum IssueType {
    PERFORMANCE,
    STYLE,
    BUG,
    SECURITY,
    DOCUMENTATION,
    INDENTATION,
    NAMING_CONVENTION,
    UNUSED_IMPORT,
    LLD_METHOD_TOO_LONG,    // Added
    LLD_TOO_MANY_PARAMETERS, // Added
    OTHER // General category
}
