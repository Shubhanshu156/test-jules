package com.example.aicodereviewer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ReviewSelectionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            Messages.showInfoMessage("Cannot perform action: No project or editor found.", "AI Code Reviewer");
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        if (!selectionModel.hasSelection()) {
            Messages.showInfoMessage("Cannot perform action: No text selected.", "AI Code Reviewer");
            return;
        }
        // Later, this will trigger review for selectionModel.getSelectedText()
        Messages.showInfoMessage("Review Selection action performed for: " + selectionModel.getSelectedText(), "AI Code Reviewer");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean enabled = false;
        if (project != null && editor != null) {
            enabled = editor.getSelectionModel().hasSelection();
        }
        e.getPresentation().setEnabled(enabled);
    }
}
