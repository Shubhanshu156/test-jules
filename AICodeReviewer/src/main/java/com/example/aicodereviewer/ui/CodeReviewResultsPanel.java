package com.example.aicodereviewer.ui;

import com.example.aicodereviewer.ai.model.AIModel;
import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.example.aicodereviewer.ai.model.AIReviewSummary;
import com.example.aicodereviewer.ai.model.IssueSeverity;
import com.example.aicodereviewer.ai.model.IssueType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CodeReviewResultsPanel extends JBPanel<CodeReviewResultsPanel> {

    private final JBLabel modelUsedLabel;
    private final JBLabel analysisTimeLabel;
    private final JBLabel issuesFoundLabel;
    private final JBLabel confidenceLabel; // Placeholder, as confidence is not in AIReviewSummary yet
    private final JBPanel<JBPanel<?>> issuesPanel;
    private final JBScrollPane scrollPane;

    public CodeReviewResultsPanel() {
        super(new BorderLayout(JBUI.scale(5), JBUI.scale(5)));
        setBorder(JBUI.Borders.empty(10));

        // Top Section: Title and Action Buttons
        JBPanel<JBPanel<?>> topPanel = new JBPanel<>(new BorderLayout());
        JBLabel titleLabel = new JBLabel("AI Code Review Results");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + JBUI.scale(2f)));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JBPanel<JBPanel<?>> actionButtonsPanel = new JBPanel<>(new FlowLayout(FlowLayout.RIGHT, JBUI.scale(5), 0));
        JButton refreshButton = new JButton("Refresh", AllIcons.Actions.Refresh);
        JButton settingsButton = new JButton("Settings", AllIcons.General.Settings);
        JButton closeButton = new JButton("Close", AllIcons.Actions.Close);

        // Placeholder actions
        refreshButton.addActionListener(e -> System.out.println("Refresh clicked"));
        settingsButton.addActionListener(e -> System.out.println("Settings clicked"));
        closeButton.addActionListener(e -> System.out.println("Close clicked"));

        actionButtonsPanel.add(refreshButton);
        actionButtonsPanel.add(settingsButton);
        actionButtonsPanel.add(closeButton);
        topPanel.add(actionButtonsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Info Bar Section
        JBPanel<JBPanel<?>> infoBarPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, JBUI.scale(10), JBUI.scale(5)));
        infoBarPanel.setBorder(IdeBorderFactory.createBorder(JBColor.border(), SideBorder.BOTTOM));
        modelUsedLabel = new JBLabel("Model Used: -");
        analysisTimeLabel = new JBLabel("Analysis Time: -");
        issuesFoundLabel = new JBLabel("Issues Found: -");
        confidenceLabel = new JBLabel("Confidence: -"); // Placeholder

        infoBarPanel.add(modelUsedLabel);
        infoBarPanel.add(analysisTimeLabel);
        infoBarPanel.add(issuesFoundLabel);
        infoBarPanel.add(confidenceLabel);

        // Main Results Area
        issuesPanel = new JBPanel<>(new VerticalLayout(JBUI.scale(10)));
        issuesPanel.setBorder(JBUI.Borders.empty(10));
        scrollPane = new JBScrollPane(issuesPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(IdeBorderFactory.createBorder(JBColor.border(), SideBorder.TOP));

        JBPanel<JBPanel<?>> centerPanel = new JBPanel<>(new BorderLayout());
        centerPanel.add(infoBarPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Initialize with sample data for layout verification
        // ApplicationManager.getApplication().invokeLater(this::showSampleData);
    }

    public void updateResults(AIReviewResponse response, AIModel modelUsed, double actualAnalysisTimeSec, int confidencePercent) {
        ApplicationManager.getApplication().invokeLater(() -> {
            modelUsedLabel.setText("Model Used: " + modelUsed.toString() + " (Auto-selected)"); // Adapt auto-selected text later
            analysisTimeLabel.setText(String.format("Analysis Time: %.1fs", actualAnalysisTimeSec));

            AIReviewSummary summary = response.getSummary();
            if (summary != null) {
                issuesFoundLabel.setText("Issues Found: " + summary.getTotalIssues());
                 // confidenceLabel.setText("Confidence: " + confidencePercent + "%"); // Assuming confidence is passed
            } else {
                issuesFoundLabel.setText("Issues Found: " + response.getIssues().size()); // Fallback if summary is null
                 // confidenceLabel.setText("Confidence: " + confidencePercent + "%");
            }
            confidenceLabel.setText("Confidence: " + confidencePercent + "%");


            issuesPanel.removeAll();
            if (response.getIssues().isEmpty()) {
                issuesPanel.add(new JBLabel("No issues found. Great job!", UIUtil.ComponentStyle.LARGE, UIUtil.FontColor.NORMAL));
            } else {
                displayIssues(response.getIssues(), summary);
            }
            issuesPanel.revalidate();
            issuesPanel.repaint();
            scrollPane.getViewport().setViewPosition(new Point(0,0)); // Scroll to top
        });
    }

    private String formatSeverity(IssueSeverity severity) {
        switch (severity) {
            case CRITICAL: return "<html><font color='red'>🔴 CRITICAL</font></html>";
            case WARNING: return "<html><font color='orange'>🟡 WARNING</font></html>";
            case SUGGESTION: return "<html><font color='blue'>🔵 SUGGESTION</font></html>";
            case INFO: return "<html><font color='gray'>⚪ INFO</font></html>";
            default: return severity.toString();
        }
    }

    private void displayIssues(List<AIReviewIssue> issues, AIReviewSummary summary) {
        Map<IssueSeverity, List<AIReviewIssue>> groupedIssues = new EnumMap<>(IssueSeverity.class);
        for (AIReviewIssue issue : issues) {
            groupedIssues.computeIfAbsent(issue.getSeverity(), k -> new ArrayList<>()).add(issue);
        }

        // Display order: CRITICAL, WARNING, SUGGESTION, INFO
        addIssuesForSeverity(IssueSeverity.CRITICAL, groupedIssues.getOrDefault(IssueSeverity.CRITICAL, Collections.emptyList()), summary);
        addIssuesForSeverity(IssueSeverity.WARNING, groupedIssues.getOrDefault(IssueSeverity.WARNING, Collections.emptyList()), summary);
        addIssuesForSeverity(IssueSeverity.SUGGESTION, groupedIssues.getOrDefault(IssueSeverity.SUGGESTION, Collections.emptyList()), summary);
        addIssuesForSeverity(IssueSeverity.INFO, groupedIssues.getOrDefault(IssueSeverity.INFO, Collections.emptyList()), summary);
    }

    private void addIssuesForSeverity(IssueSeverity severity, List<AIReviewIssue> issuesList, AIReviewSummary summary) {
        if (issuesList.isEmpty()) {
            return;
        }

        int count = 0;
        if (summary != null) {
            switch (severity) {
                case CRITICAL: count = summary.getCriticalIssues(); break;
                case WARNING: count = summary.getWarningIssues(); break;
                case SUGGESTION: count = summary.getSuggestionIssues(); break;
                case INFO: count = summary.getInfoIssues(); break;
            }
        } else {
            count = issuesList.size(); // Fallback if summary is null
        }

        JBLabel categoryLabel = new JBLabel(formatSeverity(severity) + " (" + count + ")");
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, categoryLabel.getFont().getSize() + JBUI.scale(1f)));
        issuesPanel.add(categoryLabel);

        for (AIReviewIssue issue : issuesList) {
            JBPanel<JBPanel<?>> issueCard = new JBPanel<>(new VerticalLayout(JBUI.scale(3)));
            issueCard.setBorder(IdeBorderFactory.createRoundedBorder(JBUI.scale(5), JBUI.scale(1)));
            issueCard.setBackground(UIUtil.getTextFieldBackground()); // Slightly different background

            JBLabel issueTitle = new JBLabel("<html><b>" + UIUtil.escapeXml(issue.getMessage()) + "</b></html>");
            issueTitle.setCopyable(true);
            issueCard.add(issueTitle);

            String fileAndLine = issue.getFilePath() != null ? issue.getFilePath() + ":" + issue.getLineStart() : "N/A";
            JBLabel fileLabel = new JBLabel("<html><i>" + fileAndLine + " [" + issue.getType().toString() + "]</i></html>");
            issueCard.add(fileLabel);

            if (issue.getSuggestedFix() != null && !issue.getSuggestedFix().isEmpty()) {
                JBLabel solutionLabel = new JBLabel("<html>💡 <b>Solution:</b> " + UIUtil.escapeXml(issue.getSuggestedFix()) + "</html>");
                solutionLabel.setCopyable(true);
                issueCard.add(solutionLabel);
            }

            HyperlinkLabel detailsLink = new HyperlinkLabel("📖 Detailed explanation available");
            detailsLink.addHyperlinkListener(new HyperlinkAdapter() {
                @Override
                protected void hyperlinkActivated(HyperlinkEvent e) {
                    // Placeholder: Show full explanation, perhaps in a dialog or separate panel
                    JOptionPane.showMessageDialog(CodeReviewResultsPanel.this,
                        "Detailed Explanation:\n" + issue.getExplanation(),
                        "Issue Details",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            if (issue.getExplanation() == null || issue.getExplanation().trim().isEmpty()) {
                detailsLink.setVisible(false);
            }
            issueCard.add(detailsLink);

            issuesPanel.add(issueCard);
        }
        issuesPanel.add(Box.createVerticalStrut(JBUI.scale(10))); // Spacer after category
    }

    public void showSampleData() {
        List<AIReviewIssue> sampleIssues = new ArrayList<>();
        sampleIssues.add(new AIReviewIssue("src/main/java/com/example/UserService.java", 45, null, IssueType.SECURITY, IssueSeverity.CRITICAL, "SQL Injection Vulnerability", "User input directly used in SQL query. This can lead to SQL injection attacks.", "Use PreparedStatement with parameters. Sanitize all inputs."));
        sampleIssues.add(new AIReviewIssue("src/main/java/com/example/ProductService.java", 102, null, IssueType.PERFORMANCE, IssueSeverity.WARNING, "Inefficient loop causing N+1 query problem.", "The current loop fetches related data one by one. This is highly inefficient.", "Use batch fetching (e.g., JOIN FETCH in JPA or load all necessary data in one go)."));
        sampleIssues.add(new AIReviewIssue("src/main/java/com/example/Order.java", 33, null, IssueType.STYLE, IssueSeverity.SUGGESTION, "Method name 'calculate_total' does not follow Java naming conventions.", "Method names should be in camelCase.", "Rename to 'calculateTotal'."));
        sampleIssues.add(new AIReviewIssue("src/main/java/com/example/Helper.java", 78, null, IssueType.DOCUMENTATION, IssueSeverity.INFO, "Public method 'processData' lacks Javadoc.", "All public methods should have proper Javadoc documentation.", "Add Javadoc explaining what the method does, its parameters, and what it returns."));
        sampleIssues.add(new AIReviewIssue("src/main/java/com/example/Config.java", 15, null, IssueType.BUG, IssueSeverity.CRITICAL, "NullPointer dereference possible if config file not found.", "The code does not check for null before using 'properties.getProperty()'.", "Add a null check for 'properties' or ensure 'loadProperties' throws an exception if loading fails."));

        AIReviewSummary summary = new AIReviewSummary();
        summary.setCriticalIssues(2);
        summary.setWarningIssues(1);
        summary.setSuggestionIssues(1);
        summary.setInfoIssues(1);
        summary.setTotalFilesAnalyzed(3);
        summary.setAnalysisDurationMs(2300);

        AIReviewResponse sampleResponse = new AIReviewResponse("sample-id", "timestamp", summary, sampleIssues);
        updateResults(sampleResponse, AIModel.CLAUDE, 2.3, 94);
    }

    // Main method for testing (optional, can be removed)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Code Review Results Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            CodeReviewResultsPanel panel = new CodeReviewResultsPanel();
            panel.showSampleData(); // Load sample data
            frame.setContentPane(panel);
            frame.setPreferredSize(new Dimension(800, 600));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
