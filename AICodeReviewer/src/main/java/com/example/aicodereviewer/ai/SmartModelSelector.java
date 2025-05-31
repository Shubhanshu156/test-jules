package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIModel;
import com.example.aicodereviewer.ai.model.CodeAnalysisContext;
import com.example.aicodereviewer.settings.AIReviewerSettingsState;

public class SmartModelSelector {

    public AIModel selectBestModel(CodeAnalysisContext context, AIReviewerSettingsState settings) {
        AIModel preferredModel;

        switch (context.getAnalysisType()) {
            case SECURITY:
                preferredModel = AIModel.CLAUDE;
                break;
            case PERFORMANCE:
                preferredModel = AIModel.GEMINI;
                break;
            case GENERAL_QUALITY:
                preferredModel = AIModel.OPENAI_GPT4;
                break;
            case DOCUMENTATION:
                preferredModel = AIModel.CLAUDE;
                break;
            case COMPLEX_ARCHITECTURE:
                preferredModel = AIModel.ENSEMBLE; // Assuming Ensemble might be a future feature or meta-model
                break;
            default:
                preferredModel = AIModel.OPENAI_GPT4; // Default to general quality
        }

        // Check API key availability and fallback
        switch (preferredModel) {
            case CLAUDE:
                if (isApiKeyAvailable(settings.claudeApiKey)) {
                    return AIModel.CLAUDE;
                }
                // Fallback
                if (isApiKeyAvailable(settings.openAiApiKey)) {
                    return AIModel.OPENAI_GPT4;
                }
                if (isApiKeyAvailable(settings.geminiApiKey)) {
                    return AIModel.GEMINI;
                }
                break;
            case GEMINI:
                if (isApiKeyAvailable(settings.geminiApiKey)) {
                    return AIModel.GEMINI;
                }
                // Fallback
                if (isApiKeyAvailable(settings.openAiApiKey)) {
                    return AIModel.OPENAI_GPT4;
                }
                if (isApiKeyAvailable(settings.claudeApiKey)) {
                    return AIModel.CLAUDE;
                }
                break;
            case OPENAI_GPT4:
                if (isApiKeyAvailable(settings.openAiApiKey)) {
                    return AIModel.OPENAI_GPT4;
                }
                // Fallback
                if (isApiKeyAvailable(settings.geminiApiKey)) {
                    return AIModel.GEMINI;
                }
                if (isApiKeyAvailable(settings.claudeApiKey)) {
                    return AIModel.CLAUDE;
                }
                break;
            case ENSEMBLE:
                // For Ensemble, we might need multiple keys or a specific setup.
                // For now, let's assume it can function if at least one key is available,
                // or it has its own key (not defined in this scope).
                // This logic might need to be more sophisticated.
                // If Ensemble requires specific keys and they aren't set, it should fall back.
                // For simplicity, if any key is available, we assume Ensemble can try something.
                // This is a placeholder for more complex logic.
                if (isApiKeyAvailable(settings.openAiApiKey) || isApiKeyAvailable(settings.geminiApiKey) || isApiKeyAvailable(settings.claudeApiKey)) {
                    return AIModel.ENSEMBLE;
                }
                // Fallback from ENSEMBLE if no keys are available for its potential sub-models
                if (isApiKeyAvailable(settings.openAiApiKey)) {
                    return AIModel.OPENAI_GPT4;
                }
                if (isApiKeyAvailable(settings.geminiApiKey)) {
                    return AIModel.GEMINI;
                }
                if (isApiKeyAvailable(settings.claudeApiKey)) {
                    return AIModel.CLAUDE;
                }
                break;
        }

        // If no preferred model is available due to missing keys, try a general fallback order
        if (isApiKeyAvailable(settings.openAiApiKey)) {
            return AIModel.OPENAI_GPT4;
        }
        if (isApiKeyAvailable(settings.geminiApiKey)) {
            return AIModel.GEMINI;
        }
        if (isApiKeyAvailable(settings.claudeApiKey)) {
            return AIModel.CLAUDE;
        }

        // If no API keys are configured at all
        return AIModel.AUTO_SELECT; // Indicates user needs to configure or no model can be used
    }

    private boolean isApiKeyAvailable(String apiKey) {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
