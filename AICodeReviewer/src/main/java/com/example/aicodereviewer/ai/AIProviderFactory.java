package com.example.aicodereviewer.ai;

public class AIProviderFactory {
    public AIProvider createProvider(String type, AIProviderConfig config) {
        switch(type.toLowerCase()) {
            case "gemini": return new GeminiAIProvider(config);
            case "chatgpt": return new ChatGPTAIProvider(config);
            case "deepseek": return new DeepSeekAIProvider(config);
            default: throw new IllegalArgumentException("Unknown provider type: " + type);
        }
    }
} 