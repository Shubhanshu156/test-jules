package com.example.aicodereviewer.services;

import com.example.aicodereviewer.ai.SmartModelSelector;
import com.example.aicodereviewer.ai.model.*;
import com.example.aicodereviewer.settings.AIReviewerSettingsState;
import com.example.aicodereviewer.ui.CodeReviewResultsPanel;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RealTimeAnalysisService {

    public void triggerAnalysis(Project project, Editor editor, VirtualFile file) {
        new Task.Backgroundable(project, "AI Code Review", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("AI Analyzing code in: " + file.getName());
                indicator.setIndeterminate(false); // Make it determinate if you have stages

                // Simulate AI Interaction & Preparation
                try {
                    indicator.setFraction(0.1);
                    Thread.sleep(500); // Simulate initial setup

                    AIReviewerSettingsState settings = AIReviewerSettingsState.getInstance();
                    SmartModelSelector smartModelSelector = new SmartModelSelector();

                    // Determine programming language (basic example)
                    String language = file.getFileType().getName();
                    if ("java".equalsIgnoreCase(file.getExtension())) language = "Java";


                    CodeAnalysisContext.CodeComplexity complexity = CodeAnalysisContext.CodeComplexity.MEDIUM; // Placeholder
                    // Could try to estimate complexity based on file size, lines of code etc.
                    // For now, keeping it simple.

                    CodeAnalysisContext context = new CodeAnalysisContext(
                            language, // Or get from file type
                            complexity,
                            CodeAnalysisContext.AnalysisType.GENERAL_QUALITY // Default for this general action
                    );
                    AIModel selectedModel = smartModelSelector.selectBestModel(context, settings);
                    indicator.setText("AI Analyzing code with " + selectedModel.toString() + "...");
                    indicator.setFraction(0.3);

                    // Simulate actual analysis
                    Thread.sleep(2500); // Simulate AI processing time

                    // Create Sample Response
                    List<AIReviewIssue> sampleIssues = new ArrayList<>();
                    sampleIssues.add(new AIReviewIssue(
                            file.getPath(),
                            10,
                            null,
                            IssueType.BUG,
                            IssueSeverity.CRITICAL,
                            "Potential NullPointer dereference on line 10.",
                            "The variable 'obj' might be null here due to complex conditional logic upstream, leading to a NullPointerException if 'obj.method()' is called.",
                            "Add a null check: if (obj != null) { obj.method(); }"
                    ));
                    sampleIssues.add(new AIReviewIssue(
                            file.getPath(),
                            25,
                            null,
                            IssueType.PERFORMANCE,
                            IssueSeverity.WARNING,
                            "Inefficient string concatenation in a loop.",
                            "Using '+' for string concatenation inside a loop can lead to poor performance due to repeated string object creation.",
                            "Use StringBuilder for appending strings within the loop: StringBuilder sb = new StringBuilder(); for(...) { sb.append(str); } return sb.toString();"
                    ));
                     sampleIssues.add(new AIReviewIssue(
                            file.getPath(),
                            42,
                            null,
                            IssueType.STYLE,
                            IssueSeverity.SUGGESTION,
                            "Consider using a more descriptive variable name than 'x'.",
                            "The variable 'x' does not clearly indicate its purpose in this context.",
                            "Rename 'x' to something like 'itemCount' or 'userIndex' based on its usage."
                    ));


                    AIReviewSummary summary = new AIReviewSummary();
                    summary.setCriticalIssues(1);
                    summary.setWarningIssues(1);
                    summary.setSuggestionIssues(1);
                    summary.setTotalFilesAnalyzed(1);
                    summary.setAnalysisDurationMs(2500); // Matching simulated sleep

                    AIReviewResponse response = new AIReviewResponse("fake-response-id", "timestamp", summary, sampleIssues);
                    indicator.setFraction(0.9);
                    indicator.setText("Finalizing AI review...");
                    Thread.sleep(500);


                    // Update UI on Completion
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AI Code Review");
                        if (toolWindow != null) {
                            // Ensure the tool window is visible
                            toolWindow.show(null);
                            Content[] contents = toolWindow.getContentManager().getContents();
                            if (contents.length > 0) {
                                if (contents[0].getComponent() instanceof CodeReviewResultsPanel) {
                                    CodeReviewResultsPanel resultsPanel = (CodeReviewResultsPanel) contents[0].getComponent();
                                    resultsPanel.updateResults(response, selectedModel, 2.5, 90); // Simulated time & confidence
                                }
                            }
                        }
                        Notifications.Bus.notify(new Notification(
                                "AI Code Reviewer Notification Group", // Use the group ID from plugin.xml
                                "AI Review Complete",
                                "Found " + response.getIssues().size() + " issues in " + file.getName() + ".",
                                NotificationType.INFORMATION
                        ), project);
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ApplicationManager.getApplication().invokeLater(() ->
                        Notifications.Bus.notify(new Notification(
                                "AI Code Reviewer Notification Group",
                                "AI Review Cancelled",
                                "Code analysis was cancelled.",
                                NotificationType.WARNING
                        ), project)
                    );
                }
                 indicator.setFraction(1.0);
            }
        }.queue();
    }
}
