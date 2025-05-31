package com.example.aicodereviewer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class SuggestImprovementsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null) {
            Messages.showInfoMessage("Cannot perform action: No project found.", "AI Code Reviewer");
            return;
        }

        String codeToImprove = "";
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel.hasSelection()) {
                codeToImprove = selectionModel.getSelectedText();
            } else {
                // If no selection, could improve whole file. For now, require selection.
                Messages.showInfoMessage("No text selected. Select code to suggest improvements for.", "AI Code Reviewer");
                return;
            }
        } else {
            Messages.showInfoMessage("No editor found to get code from.", "AI Code Reviewer");
            return;
        }

        if (codeToImprove.trim().isEmpty()) {
            Messages.showInfoMessage("No code to improve.", "AI Code Reviewer");
            return;
        }
        // Later, this will trigger AI suggestions for the code.
        Messages.showInfoMessage("Suggest Improvements action performed for: " + codeToImprove, "AI Code Reviewer");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean enabled = false;
        if (project != null && editor != null) {
            // Enable if there's an editor and selected text.
            enabled = editor.getSelectionModel().hasSelection();
        }
        e.getPresentation().setEnabled(enabled);
    }
}
