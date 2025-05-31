package com.example.aicodereviewer.services;

import com.example.aicodereviewer.ai.model.FeedbackType;
import com.intellij.openapi.diagnostic.Logger;

public class FeedbackCollectorService {

    private static final Logger LOG = Logger.getInstance(FeedbackCollectorService.class);

    /**
     * Records user feedback about an AI review issue.
     *
     * @param issueId      The unique ID of the AIReviewIssue.
     * @param modelUsed    The name of the AI model that generated the issue.
     * @param helpful      Boolean indicating if the user found the suggestion helpful (can be true for SUGGESTION_APPLIED, false for FALSE_POSITIVE).
     * @param userComment  Optional textual comment from the user.
     * @param feedbackType The type of feedback.
     */
    public void recordUserFeedback(String issueId, String modelUsed, boolean helpful, String userComment, FeedbackType feedbackType) {
        // In a real application, this data would be sent to a secure backend server
        // for collection and analysis. For this example, we just log it.

        // Basic validation
        if (issueId == null || issueId.trim().isEmpty()) {
            LOG.warn("User Feedback Recorded with Invalid Issue ID. Model=" + modelUsed + ", Type=" + feedbackType);
            // Optionally, one might still record feedback if issueId is missing but other info is valuable
            // For now, we'll proceed but a real system might reject or flag this.
        }
        if (modelUsed == null || modelUsed.trim().isEmpty()) {
            modelUsed = "Unknown"; // Default if model isn't specified
        }

        String comment = (userComment == null || userComment.trim().isEmpty()) ? "N/A" : userComment;

        LOG.info("User Feedback Recorded: " +
                "Issue ID='" + issueId + '\'' +
                ", Model Used='" + modelUsed + '\'' +
                ", Helpful=" + helpful +
                ", Feedback Type=" + feedbackType +
                ", User Comment='" + comment + '\'');

        // Potential future enhancements:
        // - Store feedback locally in a database if offline.
        // - Batch feedback submissions to the backend.
        // - Include more context (e.g., code snippet hash, project ID hash) for better analysis,
        //   while respecting user privacy and PII.
    }
}
