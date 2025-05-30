package com.example.aicodereviewer.vcs;

import com.example.aicodereviewer.ai.MockAIService;
import com.example.aicodereviewer.ai.model.*;
import com.example.aicodereviewer.reporting.ReportGenerator; // Import ReportGenerator
import com.example.aicodereviewer.settings.AIReviewerSettingsState;
import com.example.aicodereviewer.ui.AIReviewResultsDialog; 
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.util.ui.UIUtil; 
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AIReviewCheckinHandler extends CheckinHandler {

    private static final Logger LOG = Logger.getInstance(AIReviewCheckinHandler.class);

    private final Project project;
    private final CheckinProjectPanel panel;
    private final MockAIService aiService = MockAIService.getInstance();

    public AIReviewCheckinHandler(Project project, CheckinProjectPanel panel) {
        this.project = project;
        this.panel = panel;
    }

    @Override
    public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        return null;
    }

    @Override
    public ReturnResult beforeCheckin() {
        AIReviewerSettingsState settings = AIReviewerSettingsState.getInstance();
        if (!settings.preCommitHookEnabled) {
            return ReturnResult.COMMIT;
        }

        Collection<Change> selectedChanges = panel.getSelectedChanges();
        if (selectedChanges.isEmpty()) {
            return ReturnResult.COMMIT;
        }

        final AtomicReference<AIReviewResponse> finalReviewResponse = new AtomicReference<>();
        final List<AIReviewIssue> allIssues = Collections.synchronizedList(new ArrayList<>());
        final AtomicReference<ReturnResult> result = new AtomicReference<>(ReturnResult.COMMIT);
        final String notificationGroup = "AI Code Reviewer Commit Notification";
        
        final AIReviewSummary aggregatedSummary = new AIReviewSummary();
        final List<AIReviewFile> processedFilesForSummary = new ArrayList<>();
        final long[] totalProcessingTime = {0};


        ProgressManager.getInstance().run(new Task.Modal(project, "Running AI Code Review...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                int processedChangeCount = 0; 
                
                List<Change> changesToAnalyze = selectedChanges.stream()
                    .filter(c -> {
                        VirtualFile vf = c.getVirtualFile();
                        if (vf == null) {
                             ContentRevision ar = c.getAfterRevision();
                             if (ar != null) vf = ar.getFile().getVirtualFile();
                        }
                        return vf != null && !vf.isDirectory() && c.getType() != Change.Type.DELETED;
                    })
                    .collect(Collectors.toList());
                int totalFilesToAnalyze = changesToAnalyze.size();

                if (totalFilesToAnalyze == 0) { 
                    aggregatedSummary.setTotalFilesAnalyzed(0);
                    finalReviewResponse.set(new AIReviewResponse(UUID.randomUUID().toString(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()), aggregatedSummary, allIssues));
                    result.set(ReturnResult.COMMIT);
                    return;
                }

                Set<IssueType> enabledChecks = new HashSet<>();
                enabledChecks.add(IssueType.UNUSED_IMPORT);
                enabledChecks.add(IssueType.INDENTATION);
                enabledChecks.add(IssueType.LLD_METHOD_TOO_LONG);     
                enabledChecks.add(IssueType.LLD_TOO_MANY_PARAMETERS); 
                
                AIReviewPreferences preferences = new AIReviewPreferences(
                    Collections.singletonList("pre-commit-rules"), 10, null, enabledChecks);

                for (Change change : changesToAnalyze) { 
                    if (indicator.isCanceled()) {
                        result.set(ReturnResult.CANCEL);
                        return;
                    }
                    
                    VirtualFile virtualFile = change.getVirtualFile(); 
                     if (virtualFile == null) { 
                        ContentRevision afterRevision = change.getAfterRevision();
                        if (afterRevision != null) {
                            virtualFile = afterRevision.getFile().getVirtualFile();
                        }
                         if (virtualFile == null) continue; 
                    }
                    
                    indicator.setText("Analyzing: " + virtualFile.getName());
                    indicator.setFraction((double) processedChangeCount / totalFilesToAnalyze);
                    processedChangeCount++;

                    String content = readFileContent(virtualFile);
                    if (content == null || content.isEmpty()) {
                        LOG.warn("Could not read content or content is empty for file: " + virtualFile.getPath());
                        continue;
                    }

                    processedFilesForSummary.add(new AIReviewFile(virtualFile.getPath()));

                    String language = virtualFile.getFileType().getName().toLowerCase();
                    AIReviewFile reviewFile = new AIReviewFile(virtualFile.getPath(), content, language);
                    AIReviewTarget target = new AIReviewTarget(AnalyzeTargetType.FILE_LIST, Collections.singletonList(reviewFile));
                    AIReviewRequest request = new AIReviewRequest(
                        UUID.randomUUID().toString(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()), target, preferences);

                    try {
                        AIReviewResponse response = aiService.analyzeCode(request);
                        if (response != null) {
                            if (response.getIssues() != null) {
                                allIssues.addAll(response.getIssues());
                            }
                            if (response.getSummary() != null) {
                                aggregatedSummary.setCriticalIssues(aggregatedSummary.getCriticalIssues() + response.getSummary().getCriticalIssues());
                                aggregatedSummary.setWarningIssues(aggregatedSummary.getWarningIssues() + response.getSummary().getWarningIssues());
                                aggregatedSummary.setSuggestionIssues(aggregatedSummary.getSuggestionIssues() + response.getSummary().getSuggestionIssues());
                                aggregatedSummary.setInfoIssues(aggregatedSummary.getInfoIssues() + response.getSummary().getInfoIssues());
                            }
                             totalProcessingTime[0] += response.getSummary() != null ? response.getSummary().getAnalysisDurationMs() : 0;
                        }
                    } catch (Exception e) {
                        LOG.error("Error calling MockAIService for file " + virtualFile.getPath(), e);
                    }
                }

                if (indicator.isCanceled()) {
                    result.set(ReturnResult.CANCEL);
                    return;
                }
                
                aggregatedSummary.setTotalFilesAnalyzed(processedFilesForSummary.size());
                aggregatedSummary.setAnalysisDurationMs(totalProcessingTime[0]);
                finalReviewResponse.set(new AIReviewResponse(UUID.randomUUID().toString(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()), aggregatedSummary, allIssues));

                if (aggregatedSummary.getCriticalIssues() > 0) {
                    result.set(ReturnResult.CANCEL);
                } else {
                    result.set(ReturnResult.CANCEL);
                }
            }
        });
        
        AIReviewResponse responseToShow = finalReviewResponse.get();

        // Save report if there are issues
        if (responseToShow != null && responseToShow.getIssues() != null && !responseToShow.getIssues().isEmpty()) {
            ReportGenerator.saveReportAsJson(project, responseToShow);
        }

        // Show dialog or notification
        if (result.get() == ReturnResult.CANCEL || (responseToShow != null && responseToShow.getSummary().getTotalIssues() > 0)) {
             UIUtil.invokeLaterIfNeeded(() -> {
                AIReviewResultsDialog dialog = new AIReviewResultsDialog(project, responseToShow != null ? responseToShow : 
                    new AIReviewResponse(UUID.randomUUID().toString(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()), new AIReviewSummary(), Collections.emptyList())
                );
                dialog.show();
            });
        } else if (responseToShow != null) { // No issues, but analysis was run
             NotificationGroupManager.getInstance().getNotificationGroup(notificationGroup)
                                .createNotification("AI Code Review", "AI Review: Analysis complete, no issues found.", NotificationType.INFORMATION)
                                .notify(project);
        }
        // If responseToShow is null (e.g., task cancelled before any file processed), do nothing extra here.

        return result.get();
    }

    private String readFileContent(VirtualFile virtualFile) {
        final AtomicReference<String> contentRef = new AtomicReference<>();
        ApplicationManager.getApplication().runReadAction(() -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                contentRef.set(document.getText());
            } else {
                try {
                    contentRef.set(new String(virtualFile.contentsToByteArray()));
                } catch (Exception e) {
                    LOG.warn("Failed to read VirtualFile content: " + virtualFile.getPath(), e);
                    contentRef.set(null);
                }
            }
        });
        return contentRef.get();
    }
    
    private String getFileNameFromChange(Change change) {
        VirtualFile vf = change.getVirtualFile();
        if (vf == null) {
            ContentRevision ar = change.getAfterRevision();
            if (ar != null) vf = ar.getFile().getVirtualFile();
        }
        if (vf == null) {
            ContentRevision br = change.getBeforeRevision();
            if (br != null) vf = br.getFile().getVirtualFile();
        }
        return vf != null ? vf.getName() : "Unknown file";
    }
}
