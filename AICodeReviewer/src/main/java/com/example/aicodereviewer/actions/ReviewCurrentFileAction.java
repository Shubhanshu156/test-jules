package com.example.aicodereviewer.actions;

import com.example.aicodereviewer.services.RealTimeAnalysisService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ReviewCurrentFileAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || editor == null || file == null) {
            Messages.showInfoMessage("Cannot perform action: No project, editor, or file found.", "AI Code Reviewer");
            return;
        }

        RealTimeAnalysisService analysisService = project.getService(RealTimeAnalysisService.class);
        if (analysisService != null) {
            analysisService.triggerAnalysis(project, editor, file);
        } else {
            Messages.showErrorDialog("RealTimeAnalysisService not available.", "AI Code Reviewer Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabled(project != null && editor != null && file != null);
    }
}
