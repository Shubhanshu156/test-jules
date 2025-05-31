package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIReviewFile;
import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeminiAIProvider extends BaseAIProvider {
    private static final String PROVIDER_NAME = "Gemini";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent";

    public GeminiAIProvider(AIProviderConfig config) { super(config); }

    @Override
    public AIReviewResponse analyzeCode(AIReviewRequest request) throws AIProviderException {
        try {
            String payload = buildRequestPayload(request);
            String url = ENDPOINT + "?key=" + config.getApiKey();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429) throw new AIProviderException("Rate limit exceeded", AIProviderException.ErrorType.RATE_LIMIT_EXCEEDED);
            if (response.statusCode() >= 400) throw new AIProviderException("Gemini API error: " + response.body(), AIProviderException.ErrorType.SERVICE_UNAVAILABLE);
            return parseResponse(response.body(), request);
        } catch (Exception e) {
            throw new AIProviderException("Gemini API call failed: " + e.getMessage(), e, AIProviderException.ErrorType.NETWORK_ERROR);
        }
    }

    @Override
    public String getProviderName() { return PROVIDER_NAME; }
    @Override
    public boolean isAvailable() { return config.getApiKey() != null && !config.getApiKey().isEmpty(); }
    @Override
    public Set<String> getRequiredConfigKeys() { return Set.of("apiKey"); }

    @Override
    protected String buildRequestPayload(AIReviewRequest request) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();

        // Build the prompt for code review
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please review the following code and identify issues. ");
        prompt.append("Focus on: bugs, security vulnerabilities, performance issues, code style, and maintainability. ");
        prompt.append("Return response in JSON format with 'issues' array containing objects with: ");
        prompt.append("'type', 'severity', 'line', 'message', 'description', 'suggestion'.\n\n");

        if (request.getAnalyzeTarget() != null && request.getAnalyzeTarget().getFiles() != null) {
            for (AIReviewFile file : request.getAnalyzeTarget().getFiles()) {
                prompt.append("File: ").append(file.getFilePath()).append("\n");
                prompt.append("```\n").append(file.getContent()).append("\n```\n\n");
            }
        }

        Map<String, Object> content = Map.of(
                "parts", List.of(Map.of("text", prompt.toString()))
        );

        payload.put("contents", List.of(content));

        return objectMapper.writeValueAsString(payload);
    }
    @Override
    protected AIReviewResponse parseResponse(String response, AIReviewRequest original) {
        // TODO: Parse Gemini response JSON into AIReviewResponse
        return null;
    }
} 