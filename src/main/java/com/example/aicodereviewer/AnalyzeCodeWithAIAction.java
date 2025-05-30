package com.example.aicodereviewer;

import com.example.aicodereviewer.ai.AICodeReviewService;
import com.example.aicodereviewer.ai.AIServiceProvider;
import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyzeCodeWithAIAction extends AnAction {

    // Direct instantiation of MockAIService is removed as per instructions.
    // private final MockAIService aiService = MockAIService.getInstance();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            Messages.showErrorDialog("Cannot perform AI code review. Project or editor not available.", "Error");
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showInfoMessage("No code selected for AI review. Please select code in the editor.", "Information");
            return;
        }

        // Get an instance of AIServiceProvider
        AIServiceProvider provider = AIServiceProvider.getInstance();
        // Get an instance of AICodeReviewService (hardcoded to "Mock" for now)
        AICodeReviewService aiService = provider.getService("Mock");

        // For a more complete request, we might want to include file path or other context
        Map<String, String> options = new HashMap<>();
        String filePath = e.getData(CommonDataKeys.VIRTUAL_FILE) != null ? e.getData(CommonDataKeys.VIRTUAL_FILE).getPath() : "current_file";
        options.put("filePath", filePath);
        // We could also add language if known, e.g., options.put("language", "Java");

        AIReviewRequest request = new AIReviewRequest(selectedText, options);

        try {
            AIReviewResponse response = aiService.analyzeCode(request);

            if (response != null && response.isSuccess()) {
                if (response.getIssues() == null || response.getIssues().isEmpty()) {
                    Messages.showInfoMessage("AI code review complete. No issues found by " + aiService.getModelInfo().getModelName() + ".", "AI Review Result");
                } else {
                    String issuesString = response.getIssues().stream()
                            .map(issue -> issue.getType() + " (Line " + issue.getLineNumber() + "): " + issue.getDescription())
                            .collect(Collectors.joining("\n"));
                    Messages.showInfoMessage("AI code review complete. Issues found by " + aiService.getModelInfo().getModelName() + ":\n" + issuesString, "AI Review Result");
                }
            } else {
                String errorMessage = "AI code review failed.";
                if (response != null && response.getErrorMesage() != null && !response.getErrorMesage().isEmpty()) {
                    errorMessage += "\nError: " + response.getErrorMesage();
                } else if (response == null) {
                    errorMessage += "\nResponse was null.";
                }
                 Messages.showErrorDialog(errorMessage + "\nService: " + aiService.getModelInfo().getModelName(), "AI Review Error");
            }
        } catch (Exception ex) {
            Messages.showErrorDialog("An error occurred during AI code review with " + aiService.getModelInfo().getModelName() + ":\n" + ex.getMessage(), "AI Review Exception");
            ex.printStackTrace(); // For debugging, should use proper logging in a real app
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable the action only if there is a project, an editor, and some text selected.
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
    }
}
