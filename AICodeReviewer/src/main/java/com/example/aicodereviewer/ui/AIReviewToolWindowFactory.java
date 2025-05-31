package com.example.aicodereviewer.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class AIReviewToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Instantiate CodeReviewResultsPanel
        CodeReviewResultsPanel resultsPanel = new CodeReviewResultsPanel();

        // It's good practice to initialize it, perhaps with a welcome message or sample data if desired
        // For now, it will be empty until an analysis is run.
        // Or, to show sample data initially for testing:
        // resultsPanel.showSampleData();

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(resultsPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
