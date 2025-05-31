package com.example.aicodereviewer.services;

import com.example.aicodereviewer.ai.model.*;
import com.example.aicodereviewer.settings.AIReviewerSettingsState;
import com.intellij.openapi.project.Project;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EnsembleAnalysisService {

    // Using a shared thread pool for simulated async operations
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public CompletableFuture<AIReviewResponse> performEnsembleAnalysis(CodeAnalysisContext context, Project project, String filePath) {
        AIReviewerSettingsState settings = AIReviewerSettingsState.getInstance();
        List<CompletableFuture<AIReviewResponse>> futures = new ArrayList<>();

        // Define target models for ensemble
        Map<String, RunnableCompletableFuture<AIReviewResponse>> modelTasks = new LinkedHashMap<>();

        // Simulate Gemini
        if (settings.geminiApiKey != null && !settings.geminiApiKey.trim().isEmpty()) {
            modelTasks.put("Gemini", () -> simulateProviderCall("Gemini", filePath,
                    new AIReviewIssue(filePath, 10, null, IssueType.BUG, IssueSeverity.CRITICAL, "NullPointer in critical path (Gemini)", "Explanation G1", "Fix G1"),
                    new AIReviewIssue(filePath, 20, null, IssueType.PERFORMANCE, IssueSeverity.WARNING, "Slow loop (Gemini)", "Explanation G2", "Fix G2")
            ));
        }

        // Simulate OpenAI
        if (settings.openAiApiKey != null && !settings.openAiApiKey.trim().isEmpty()) {
            modelTasks.put("OpenAI", () -> simulateProviderCall("OpenAI", filePath,
                    new AIReviewIssue(filePath, 10, null, IssueType.BUG, IssueSeverity.CRITICAL, "NullPointer in critical path (OpenAI)", "Explanation O1", "Fix O1"), // Same as Gemini's first
                    new AIReviewIssue(filePath, 30, null, IssueType.STYLE, IssueSeverity.SUGGESTION, "Naming convention (OpenAI)", "Explanation O2", "Fix O2")
            ));
        }

        // Simulate Claude
        if (settings.claudeApiKey != null && !settings.claudeApiKey.trim().isEmpty()) {
            modelTasks.put("Claude", () -> simulateProviderCall("Claude", filePath,
                    new AIReviewIssue(filePath, 20, null, IssueType.PERFORMANCE, IssueSeverity.WARNING, "Slow loop (Claude)", "Explanation C1", "Fix C1"), // Same as Gemini's second
                    new AIReviewIssue(filePath, 40, null, IssueType.SECURITY, IssueSeverity.CRITICAL, "SQL Injection (Claude)", "Explanation C2", "Fix C2")
            ));
        }

        if (modelTasks.isEmpty()) {
            // No models configured for ensemble, return an empty response
            return CompletableFuture.completedFuture(new AIReviewResponse(Collections.emptyList(), "No models configured for ensemble.", null, 0));
        }

        for (Map.Entry<String, RunnableCompletableFuture<AIReviewResponse>> entry : modelTasks.entrySet()) {
            futures.add(CompletableFuture.supplyAsync(entry.getValue()::run, executor));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<AIReviewIssue> allIssuesFromAllModels = new ArrayList<>();
                    long totalDurationMs = 0;

                    for (CompletableFuture<AIReviewResponse> future : futures) {
                        try {
                            AIReviewResponse response = future.get(); // Should not block here due to allOf
                            if (response != null && response.getIssues() != null) {
                                allIssuesFromAllModels.addAll(response.getIssues());
                            }
                            if (response != null && response.getSummary() != null) {
                                totalDurationMs += response.getSummary().getAnalysisDurationMs();
                            } else if (response != null && response.getModelProcessingTime() != null && !response.getModelProcessingTime().isEmpty()){
                                try {
                                    totalDurationMs += Long.parseLong(response.getModelProcessingTime().replace("ms", ""));
                                } catch (NumberFormatException e) {
                                    // ignore
                                }
                            }
                        } catch (Exception e) {
                            // Log or handle individual future failure
                            System.err.println("Error processing future result: " + e.getMessage());
                        }
                    }

                    // Aggregate results: Group by unique issue (filePath, lineStart, message, type, severity)
                    Map<Integer, AIReviewIssue> aggregatedIssuesMap = new LinkedHashMap<>();
                    for (AIReviewIssue issue : allIssuesFromAllModels) {
                        int issueHashCode = issue.hashCode(); // Using the overridden hashCode
                        AIReviewIssue existingIssue = aggregatedIssuesMap.get(issueHashCode);
                        if (existingIssue == null) {
                            // Create a new issue for aggregation to avoid modifying original simulation objects
                            AIReviewIssue newAggregatedIssue = new AIReviewIssue(
                                    issue.getFilePath(), issue.getLineStart(), issue.getLineEnd(),
                                    issue.getType(), issue.getSeverity(), issue.getMessage(),
                                    issue.getExplanation(), issue.getSuggestedFix()
                            );
                            newAggregatedIssue.addReportedByModel(getModelNameFromIssueMessage(issue.getMessage())); // Extract model from message for simulation
                            aggregatedIssuesMap.put(issueHashCode, newAggregatedIssue);
                        } else {
                            existingIssue.addReportedByModel(getModelNameFromIssueMessage(issue.getMessage()));
                            // Potentially merge explanations or other fields if desired
                        }
                    }

                    List<AIReviewIssue> finalAggregatedIssues = new ArrayList<>(aggregatedIssuesMap.values());

                    AIReviewSummary ensembleSummary = new AIReviewSummary();
                    for (AIReviewIssue issue : finalAggregatedIssues) {
                        switch (issue.getSeverity()) {
                            case CRITICAL: ensembleSummary.incrementCritical(); break;
                            case WARNING: ensembleSummary.incrementWarning(); break;
                            case SUGGESTION: ensembleSummary.incrementSuggestion(); break;
                            case INFO: ensembleSummary.incrementInfo(); break;
                        }
                    }
                    ensembleSummary.setTotalFilesAnalyzed(1); // Assuming one file context for now
                    ensembleSummary.setAnalysisDurationMs(totalDurationMs); // Sum of durations (can be refined)

                    return new AIReviewResponse("ensemble-response-id", new Date().toString(), ensembleSummary, finalAggregatedIssues);
                });
    }

    // Helper to extract model name from simulated issue messages like "...(ModelName)"
    private String getModelNameFromIssueMessage(String message) {
        if (message != null && message.endsWith(")")) {
            int openParen = message.lastIndexOf('(');
            if (openParen != -1 && openParen < message.length() -1) {
                return message.substring(openParen + 1, message.length() - 1);
            }
        }
        return "UnknownModel";
    }

    // Simulate a single provider call
    private AIReviewResponse simulateProviderCall(String modelName, String filePath, AIReviewIssue... issues) {
        try {
            Thread.sleep(1000 + new Random().nextInt(1000)); // Simulate network delay & processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        List<AIReviewIssue> issueList = Arrays.asList(issues);
        // Mark issues as reported by this model (already done by message for simulation)
        // issueList.forEach(issue -> issue.addReportedByModel(modelName)); // Not needed if message contains it

        AIReviewSummary summary = new AIReviewSummary();
        for (AIReviewIssue issue : issueList) {
             switch (issue.getSeverity()) {
                case CRITICAL: summary.incrementCritical(); break;
                case WARNING: summary.incrementWarning(); break;
                case SUGGESTION: summary.incrementSuggestion(); break;
                case INFO: summary.incrementInfo(); break;
            }
        }
        summary.setAnalysisDurationMs(1000 + new Random().nextInt(500)); // Individual model time
        summary.setTotalFilesAnalyzed(1);

        return new AIReviewResponse(modelName.toLowerCase() + "-response-id", new Date().toString(), summary, issueList);
    }

    // Functional interface for the simulation tasks
    @FunctionalInterface
    interface RunnableCompletableFuture<T> {
        T run();
    }
}
