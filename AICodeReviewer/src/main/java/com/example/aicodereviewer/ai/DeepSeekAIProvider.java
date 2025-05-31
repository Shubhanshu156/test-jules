package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DeepSeekAIProvider extends BaseAIProvider {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROVIDER_NAME = "DeepSeek";
    private static final String ENDPOINT = "https://api.deepseek.com/v1/chat/completions";

    public DeepSeekAIProvider(AIProviderConfig config) { super(config); }

    @Override
    public AIReviewResponse analyzeCode(AIReviewRequest request) throws AIProviderException {
        try {
            String payload = buildRequestPayload(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429) throw new AIProviderException("Rate limit exceeded", AIProviderException.ErrorType.RATE_LIMIT_EXCEEDED);
            if (response.statusCode() >= 400) throw new AIProviderException("DeepSeek API error: " + response.body(), AIProviderException.ErrorType.SERVICE_UNAVAILABLE);
            return parseResponse(response.body(), request);
        } catch (Exception e) {
            throw new AIProviderException("DeepSeek API call failed: " + e.getMessage(), e, AIProviderException.ErrorType.NETWORK_ERROR);
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


}