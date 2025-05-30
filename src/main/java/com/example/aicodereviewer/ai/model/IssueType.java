package com.example.aicodereviewer.ai.model;

public enum IssueType {
    // Existing refined or kept
    BUG,                          // A functional bug in the code
    SECURITY_VULNERABILITY,       // A security vulnerability (was VULNERABILITY)
    CODE_SMELL,                   // A maintainability issue or bad practice
    PERFORMANCE_ISSUE,            // A performance concern (was PERFORMANCE)
    STYLE,                        // A coding style violation
    DOCUMENTATION_ISSUE,          // Issues related to comments or documentation (was DOCUMENTATION)
    INFO,                         // General information or note
    WARNING,                      // A general warning for potential problems
    ERROR,                        // A critical error or problem preventing functionality

    // New specific types
    DEPRECATED_USAGE,             // Use of deprecated APIs or language features
    MEMORY_LEAK,                  // Potential memory leak identified
    CONCURRENCY_ISSUE,            // Issues related to multi-threading or concurrent access
    API_MISUSE,                   // Incorrect or suboptimal use of an API
    DESIGN_PATTERN_VIOLATION,     // Deviation from established design patterns
    REFACTORING_SUGGESTION        // Suggestion for code refactoring for clarity, efficiency etc.
}
