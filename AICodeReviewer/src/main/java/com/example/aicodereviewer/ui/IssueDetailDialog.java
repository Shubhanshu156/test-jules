package com.example.aicodereviewer.ui;

import com.example.aicodereviewer.ai.model.AIModel;
import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.FeedbackType;
import com.example.aicodereviewer.ai.model.IssueSeverity;
import com.example.aicodereviewer.services.FeedbackCollectorService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class IssueDetailDialog extends DialogWrapper {

    private final AIReviewIssue issue;
    private final AIModel modelUsed;
    private final Project project; // Required for Editor component

    // Data that might be missing from AIReviewIssue and passed separately or added later
    private String problematicCodeSnippet = "// Problematic code snippet here...\nint x = 10;\nif (x > 5) { System.out.println(\"Bad\"); }";
    private String potentialImpactText = "Details about potential impact...";
    private String recommendedSolutionCode = "// Recommended solution code...\nint x = 10;\nif (x > 5) {\n  // Safe action\n  System.out.println(\"Good\");\n}";
    private String whySolutionWorksText = "Explanation why this solution is effective...";
    private int confidencePercent = 98; // Example
    private String detectionTime = "0.8s"; // Example


    private EditorEx problematicCodeEditor;
    private EditorEx recommendedSolutionEditor;

    public IssueDetailDialog(@Nullable Project project, AIReviewIssue issue, AIModel modelUsed) {
        super(project, true); // true for canBeParent
        this.project = project;
        this.issue = issue;
        this.modelUsed = modelUsed;

        setTitle("Issue Details: " + issue.getMessage());
        setResizable(true);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new VerticalLayout(JBUI.scale(10)));
        dialogPanel.setBorder(JBUI.Borders.empty(10));
        dialogPanel.setPreferredSize(new Dimension(JBUI.scale(700), JBUI.scale(800)));


        // Info Header Section
        JPanel infoHeaderPanel = new JPanel(new GridLayout(0, 1, 0, JBUI.scale(2)));
        infoHeaderPanel.add(new JBLabel("Severity: " + issue.getSeverity() + " | Confidence: " + confidencePercent + "%"));
        String lineRange = issue.getLineEnd() != null ? issue.getLineStart() + "-" + issue.getLineEnd() : String.valueOf(issue.getLineStart());
        infoHeaderPanel.add(new JBLabel("File: " + issue.getFilePath() + " | Line: " + lineRange));
        infoHeaderPanel.add(new JBLabel("Model: " + modelUsed.toString() + " | Detection Time: " + detectionTime));
        dialogPanel.add(infoHeaderPanel);
        dialogPanel.add(new JSeparator());

        // Problematic Code Section
        dialogPanel.add(createSectionLabel("PROBLEMATIC CODE:"));
        problematicCodeEditor = createEditor(problematicCodeSnippet);
        dialogPanel.add(problematicCodeEditor.getComponent());


        // Vulnerability Explanation Section
        dialogPanel.add(createSectionLabel("VULNERABILITY EXPLANATION:"));
        dialogPanel.add(createReadOnlyTextArea(issue.getExplanation() != null ? issue.getExplanation() : "No detailed explanation provided."));

        // Potential Impact Section
        dialogPanel.add(createSectionLabel("POTENTIAL IMPACT:"));
        dialogPanel.add(createReadOnlyTextArea(potentialImpactText));

        // Recommended Solution Section
        dialogPanel.add(createSectionLabel("RECOMMENDED SOLUTION:"));
        recommendedSolutionEditor = createEditor(issue.getSuggestedFix() != null ? issue.getSuggestedFix() : recommendedSolutionCode);
        dialogPanel.add(recommendedSolutionEditor.getComponent());

        // Why This Works Section
        dialogPanel.add(createSectionLabel("WHY THIS WORKS:"));
        dialogPanel.add(createReadOnlyTextArea(whySolutionWorksText));

        return dialogPanel;
    }

    private JBLabel createSectionLabel(String text) {
        JBLabel label = new JBLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(JBUI.Borders.emptyTop(8));
        return label;
    }

    private JComponent createReadOnlyTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UIUtil.getLabelFont());
        textArea.setBackground(UIUtil.getPanelBackground());
        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(JBUI.scale(650), JBUI.scale(80)));
        return scrollPane;
    }

    private EditorEx createEditor(String text) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        org.jetbrains.annotations.NotNull var document = editorFactory.createDocument(text);
        EditorEx editor = (EditorEx) editorFactory.createEditor(document, project, FileTypes.PLAIN_TEXT, true);

        EditorSettings settings = editor.getSettings();
        settings.setUseSoftWraps(true);
        settings.setLineNumbersShown(true);
        settings.setIndentGuidesShown(false);
        settings.setFoldingOutlineShown(false);
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalColumnsCount(0);
        settings.setRightMarginShown(false);
        editor.getComponent().setPreferredSize(new Dimension(JBUI.scale(650), JBUI.scale(120)));
        return editor;
    }


    @Override
    protected Action @NotNull [] createActions() {
        // OKAction is fine as a "Close" button
        return new Action[]{getOKAction()};
    }

    @Override
    protected JComponent createSouthPanel() {
        JComponent southPanel = super.createSouthPanel(); // This will include OK/Cancel or just OK if overridden createActions
        JPanel customButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton applyFixButton = new JButton("Apply Fix");
        applyFixButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Apply Fix clicked (placeholder)"));

        JButton markAsFalsePositiveButton = new JButton("Mark as False Positive");
        markAsFalsePositiveButton.addActionListener(e -> {
            FeedbackCollectorService feedbackService = ApplicationManager.getApplication().getService(FeedbackCollectorService.class);
            if (feedbackService != null) {
                feedbackService.recordUserFeedback(
                        issue.getId(),
                        modelUsed.toString(),
                        false, // Not helpful in the sense of being a true positive
                        "User marked as False Positive from Issue Detail Dialog.",
                        FeedbackType.FALSE_POSITIVE
                );
                JOptionPane.showMessageDialog(this.getContentPane(), "Feedback (False Positive) submitted for issue: " + issue.getId(), "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this.getContentPane(), "Error: Feedback service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
            }
            // Optionally close the dialog after submitting feedback
            // close(DialogWrapper.OK_EXIT_CODE);
        });

        JButton learnMoreButton = new JButton("Learn More");
        learnMoreButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Learn More clicked (placeholder)"));

        customButtonsPanel.add(applyFixButton);
        customButtonsPanel.add(markAsFalsePositiveButton);
        customButtonsPanel.add(learnMoreButton);

        if (southPanel instanceof JPanel) {
            ((JPanel) southPanel).add(customButtonsPanel, BorderLayout.WEST); // Add custom buttons to the left of standard OK/Cancel
        }
        return southPanel;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (problematicCodeEditor != null && !problematicCodeEditor.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(problematicCodeEditor);
        }
        if (recommendedSolutionEditor != null && !recommendedSolutionEditor.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(recommendedSolutionEditor);
        }
    }

    // Example of how to show the dialog (for testing or actual use)
    public static void showDialog(Project project, AIReviewIssue issue, AIModel modelUsed) {
        IssueDetailDialog dialog = new IssueDetailDialog(project, issue, modelUsed);
        dialog.show();
    }
}
