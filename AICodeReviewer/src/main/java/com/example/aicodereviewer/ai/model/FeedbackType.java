package com.example.aicodereviewer.ai.model;

public enum FeedbackType {
    GENERAL_HELPFUL,    // User found the suggestion generally helpful
    GENERAL_UNHELPFUL,  // User found the suggestion generally unhelpful
    FALSE_POSITIVE,     // User marked the issue as a false positive
    SUGGESTION_APPLIED, // User indicates they applied the suggested fix
    ACCURATE_SUGGESTION, // User confirms the suggestion is accurate but might not apply it
    INACCURATE_SUGGESTION, // User confirms the suggestion is inaccurate
    OTHER               // Any other type of feedback
}
