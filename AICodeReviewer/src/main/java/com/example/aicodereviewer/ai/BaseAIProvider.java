package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAIProvider implements AIProvider {
    protected final AIProviderConfig config;
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    public BaseAIProvider(AIProviderConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    }

    protected abstract String buildRequestPayload(AIReviewRequest request) throws JsonProcessingException;
    protected AIReviewResponse parseResponse(String responseBody, AIReviewRequest originalRequest)
            throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        List<AIReviewIssue> issues = new ArrayList<>();

        // Parse Gemini response structure
        JsonNode candidates = root.get("candidates");
        if (candidates != null && candidates.isArray() && candidates.size() > 0) {
            JsonNode content = candidates.get(0).get("content");
            if (content != null) {
                JsonNode parts = content.get("parts");
                if (parts != null && parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).get("text").asText();
                    issues.addAll(parseAIGeneratedIssues(text, originalRequest));
                }
            }
        }

        AIReviewSummary summary = new AIReviewSummary();
        summary.setTotalFilesAnalyzed(originalRequest.getAnalyzeTarget().getFiles().size());
        categorizeIssues(issues, summary);

        return new AIReviewResponse(
                originalRequest.getRequestId(),
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                summary,
                issues
        );
    }
    private List<AIReviewIssue> parseAIGeneratedIssues(String aiResponse, AIReviewRequest request) {
        List<AIReviewIssue> issues = new ArrayList<>();

        try {
            // Try to extract JSON from the AI response
            String jsonPart = extractJsonFromResponse(aiResponse);
            if (jsonPart != null) {
                JsonNode jsonResponse = objectMapper.readTree(jsonPart);
                JsonNode issuesNode = jsonResponse.get("issues");

                if (issuesNode != null && issuesNode.isArray()) {
                    for (JsonNode issueNode : issuesNode) {
                        AIReviewIssue issue = parseIssueFromJson(issueNode, request);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: create a generic issue if parsing fails
            issues.add(new AIReviewIssue(
                    "unknown", 1, null, IssueType.BUG, IssueSeverity.INFO,
                    "AI analysis completed but response parsing failed",
                    "The AI provider returned a response but it couldn't be parsed properly",
                    "Review the raw AI response manually: " + aiResponse.substring(0, Math.min(200, aiResponse.length()))
            ));
        }

        return issues;
    }
    private String extractJsonFromResponse(String response) {
        // Look for JSON block in the response
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return response.substring(jsonStart, jsonEnd + 1);
        }
        return null;
    }
    private void categorizeIssues(List<AIReviewIssue> issues, AIReviewSummary summary) {
        for (AIReviewIssue issue : issues) {
            switch (issue.getSeverity()) {
                case CRITICAL:
                    summary.incrementCritical();
                    break;
                case WARNING:
                    summary.incrementWarning();
                    break;
                case SUGGESTION:
                    summary.incrementSuggestion();
                    break;
                case INFO:
                default:
                    break;
            }
        }
    }
    private AIReviewIssue parseIssueFromJson(JsonNode issueNode, AIReviewRequest request) {
        try {
            String type = issueNode.get("type").asText();
            String severity = issueNode.get("severity").asText();
            int line = issueNode.get("line").asInt(1);
            String message = issueNode.get("message").asText();
            String description = issueNode.get("description").asText();
            String suggestion = issueNode.get("suggestion").asText();

            // Determine file path (use first file as default)
            String filePath = "unknown";
            if (request.getAnalyzeTarget() != null &&
                    request.getAnalyzeTarget().getFiles() != null &&
                    !request.getAnalyzeTarget().getFiles().isEmpty()) {
                filePath = request.getAnalyzeTarget().getFiles().get(0).getFilePath();
            }

            IssueType issueType = mapStringToIssueType(type);
            IssueSeverity issueSeverity = mapStringToIssueSeverity(severity);

            return new AIReviewIssue(filePath, line, null, issueType, issueSeverity,
                    message, description, suggestion);

        } catch (Exception e) {
            return null;
        }
    }
    private IssueType mapStringToIssueType(String type) {
        try {
            return IssueType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return IssueType.BUG; // Default fallback
        }
    }

    private IssueSeverity mapStringToIssueSeverity(String severity) {
        try {
            return IssueSeverity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return IssueSeverity.INFO; // Default fallback
        }
    }
} 