package com.example.aicodereviewer;

import com.example.aicodereviewer.ai.MockAIService;
import com.example.aicodereviewer.ai.model.*;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

public class AnalyzeCodeWithAIAction extends AnAction {

    private final MockAIService aiService = MockAIService.getInstance();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE); // Get VirtualFile for file path and type

        if (project == null || editor == null || virtualFile == null) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("AI Code Reviewer Notification Group")
                    .createNotification("AI Code Reviewer", "No active editor, project, or file found.", NotificationType.ERROR)
                    .notify(project);
            return;
        }

        Document document = editor.getDocument();
        String codeSnippet = document.getText();

        if (codeSnippet.isEmpty()) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("AI Code Reviewer Notification Group")
                    .createNotification("AI Code Reviewer", "Editor is empty. Nothing to analyze.", NotificationType.WARNING)
                    .notify(project);
            return;
        }

        // Construct AIReviewRequest
        String requestId = UUID.randomUUID().toString();
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        FileType fileType = virtualFile.getFileType();
        String language = fileType.getName().toLowerCase(); // Simplistic language detection

        AIReviewFile reviewFile = new AIReviewFile(virtualFile.getPath(), codeSnippet, language);
        AIReviewTarget target = new AIReviewTarget(AnalyzeTargetType.FILE_LIST, Collections.singletonList(reviewFile));
        
        // Hardcoded preferences for now
        AIReviewPreferences preferences = new AIReviewPreferences(
            Collections.singletonList("general-best-practices"), 
            10, 
            Collections.singletonList("build/"),
            null // enabledChecks
        );

        AIReviewRequest request = new AIReviewRequest(requestId, timestamp, target, preferences);

        // Call the service
        AIReviewResponse response = aiService.analyzeCode(request);

        // Display summary from response
        AIReviewSummary summary = response.getSummary();
        String summaryMessage = String.format(
                "Analysis Complete. Duration: %d ms\nCritical: %d, Warnings: %d, Suggestions: %d, Info: %d\nFiles Analyzed: %d",
                summary.getAnalysisDurationMs(),
                summary.getCriticalIssues(),
                summary.getWarningIssues(),
                summary.getSuggestionIssues(),
                summary.getInfoIssues(),
                summary.getTotalFilesAnalyzed()
        );

        NotificationType notificationType;
        if (summary.getCriticalIssues() > 0) {
            notificationType = NotificationType.ERROR;
        } else if (summary.getWarningIssues() > 0) {
            notificationType = NotificationType.WARNING;
        } else {
            notificationType = NotificationType.INFORMATION;
        }

        NotificationGroupManager.getInstance()
                .getNotificationGroup("AI Code Reviewer Notification Group")
                .createNotification("AI Code Analysis Summary", summaryMessage, notificationType)
                .notify(project);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        // Action is enabled if there's a project, an editor, and an open file
        e.getPresentation().setEnabledAndVisible(project != null && editor != null && virtualFile != null);
    }
}
