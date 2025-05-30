package com.example.aicodereviewer.ai;

// Import AIModelInfo if needed for future, not strictly for current structure
// import com.example.aicodereviewer.ai.model.AIModelInfo;

public class AIServiceProvider {

    private static AIServiceProvider instance;

    private AIServiceProvider() {
        // Private constructor to prevent direct instantiation
    }

    public static AIServiceProvider getInstance() {
        if (instance == null) {
            instance = new AIServiceProvider();
        }
        return instance;
    }

    /**
     * Returns an AICodeReviewService implementation based on the preferred model name.
     * For now, it defaults to MockAIService. Other services will be implemented later.
     *
     * @param preferredModelName The name of the preferred AI model.
     * @return An instance of AICodeReviewService.
     */
    public AICodeReviewService getService(String preferredModelName) {
        if (preferredModelName == null) {
            return MockAIService.getInstance();
        }

        switch (preferredModelName.toLowerCase()) {
            case "gemini":
                // TODO: return new GeminiAIService();
                return MockAIService.getInstance(); // Placeholder
            case "openai":
                // TODO: return new OpenAIService();
                return MockAIService.getInstance(); // Placeholder
            case "claude":
                // TODO: return new ClaudeAIService();
                return MockAIService.getInstance(); // Placeholder
            case "mock":
            default:
                return MockAIService.getInstance();
        }
    }
}
