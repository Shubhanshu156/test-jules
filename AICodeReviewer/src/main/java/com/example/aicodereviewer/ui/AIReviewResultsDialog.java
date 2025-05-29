package com.example.aicodereviewer.ui;

import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.example.aicodereviewer.ai.model.AIReviewSummary;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

public class AIReviewResultsDialog extends DialogWrapper {

    private final Project project;
    private final AIReviewResponse reviewResponse;
    private JBTable issuesTable;

    public AIReviewResultsDialog(@Nullable Project project, AIReviewResponse reviewResponse) {
        super(project, true); // true = canBeParent
        this.project = project;
        this.reviewResponse = reviewResponse;
        setTitle("AI Code Review Results");
        setOKButtonText("OK"); // Only one button
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout(0, JBUI.scale(10))); // Overall panel
        dialogPanel.setBorder(new EmptyBorder(JBUI.scale(10), JBUI.scale(10), JBUI.scale(10), JBUI.scale(10)));

        // Summary Panel
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(2);

        AIReviewSummary summary = reviewResponse.getSummary();
        summaryPanel.add(new JBLabel("Analysis Summary:"), gbc);
        gbc.gridy++;
        summaryPanel.add(new JBLabel(String.format("Critical Issues: %d", summary.getCriticalIssues())), gbc);
        gbc.gridy++;
        summaryPanel.add(new JBLabel(String.format("Warnings: %d", summary.getWarningIssues())), gbc);
        gbc.gridy++;
        summaryPanel.add(new JBLabel(String.format("Suggestions: %d", summary.getSuggestionIssues())), gbc);
        gbc.gridy++;
        summaryPanel.add(new JBLabel(String.format("Files Analyzed: %d", summary.getTotalFilesAnalyzed())), gbc);
        gbc.gridy++;
        summaryPanel.add(new JBLabel(String.format("Duration: %d ms", summary.getAnalysisDurationMs())), gbc);

        dialogPanel.add(summaryPanel, BorderLayout.NORTH);

        // Issues Table
        List<AIReviewIssue> issues = reviewResponse.getIssues() != null ? reviewResponse.getIssues() : Collections.emptyList();
        IssuesTableModel tableModel = new IssuesTableModel(issues);
        issuesTable = new JBTable(tableModel);
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesTable.setStriped(true);
        issuesTable.getEmptyText().setText("No issues found or analysis not performed.");
        
        // Configure column widths (optional, but can improve appearance)
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(JBUI.scale(80)); // Severity
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(JBUI.scale(150)); // File
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(JBUI.scale(50));  // Line
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(JBUI.scale(400)); // Message


        // Double-click navigation listener
        issuesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToSelectedIssue();
                }
            }
        });

        JBScrollPane scrollPane = new JBScrollPane(issuesTable);
        scrollPane.setPreferredSize(new Dimension(JBUI.scale(700), JBUI.scale(300)));
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Optional: "Go to Source" button (could be added to southPanel with createSouthPanel)
        // For now, relying on double-click.

        return dialogPanel;
    }

    private void navigateToSelectedIssue() {
        if (project == null) return;
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow >= 0) {
            IssuesTableModel model = (IssuesTableModel) issuesTable.getModel();
            AIReviewIssue issue = model.getIssueAt(selectedRow);

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(issue.getFilePath());
            if (virtualFile != null) {
                // Line numbers are usually 1-based in UIs, OpenFileDescriptor expects 0-based for line if not column.
                // However, for navigating to a line, it's often simpler to pass line - 1.
                // OpenFileDescriptor(project, file, line, column)
                // For just line: OpenFileDescriptor(project, file, offset) or OpenFileDescriptor(project, file, line, -1)
                // Let's use line-1 for 0-based line index.
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, issue.getLineStart() - 1, 0);
                FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
            } else {
                // Handle file not found
                UIUtil.invokeLaterIfNeeded(() -> {
                     Messages.showErrorDialog(project, "Could not find file: " + issue.getFilePath(), "Navigation Error");
                });
            }
        }
    }
    
    @Override
    protected JComponent createSouthPanel() {
        // Override to provide only OK button, or OK and custom "Go to Source"
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(getOKAction());
        
        JButton goToSourceButton = new JButton("Go to Source");
        goToSourceButton.setEnabled(false); // Initially disabled
        goToSourceButton.addActionListener(e -> navigateToSelectedIssue());

        if (issuesTable != null) { // Ensure table is initialized
            issuesTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    goToSourceButton.setEnabled(issuesTable.getSelectedRow() >= 0);
                }
            });
        }
        
        southPanel.add(goToSourceButton);
        southPanel.add(okButton);
        return southPanel;
    }


    // We only want an OK button, DialogWrapper provides it by default if createActions() isn't overridden
    // or if it returns new Action[]{getOKAction()};
    // Overriding createSouthPanel gives more control if we add custom buttons like "Go to Source".
    // If only OK is needed, then createSouthPanel and createActions can be omitted for default behavior.
    @Override
    protected Action[] createActions() {
        // Return empty if we are using createSouthPanel to manage buttons.
        // Or, return just getOKAction() if not using createSouthPanel for custom buttons.
        // Since createSouthPanel is customized, let it handle buttons.
        return new Action[0]; 
    }
}
