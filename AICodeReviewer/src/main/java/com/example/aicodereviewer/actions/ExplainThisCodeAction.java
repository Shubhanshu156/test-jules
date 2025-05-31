package com.example.aicodereviewer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ExplainThisCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null) {
            Messages.showInfoMessage("Cannot perform action: No project found.", "AI Code Reviewer");
            return;
        }

        String codeToExplain = "";
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel.hasSelection()) {
                codeToExplain = selectionModel.getSelectedText();
            } else {
                // If no selection, consider explaining the whole file or doing nothing
                // For now, let's require selection for explain, or could use whole file.
                // codeToExplain = editor.getDocument().getText();
                Messages.showInfoMessage("No text selected. Select code to explain.", "AI Code Reviewer");
                return;
            }
        } else {
             Messages.showInfoMessage("No editor found to get code from.", "AI Code Reviewer");
            return;
        }

        if (codeToExplain.trim().isEmpty()) {
            Messages.showInfoMessage("No code to explain.", "AI Code Reviewer");
            return;
        }

        // Later, this will trigger AI explanation for the code.
        Messages.showInfoMessage("Explain This Code action performed for: " + codeToExplain, "AI Code Reviewer");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean enabled = false;
        if (project != null && editor != null) {
            // Enable if there's an editor (implying a file is open)
            // More specifically, enable if there is selected text.
            enabled = editor.getSelectionModel().hasSelection();
        }
        e.getPresentation().setEnabled(enabled);
    }
}
