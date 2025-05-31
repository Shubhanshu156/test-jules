package com.example.aicodereviewer.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AIReviewerSettingsConfigurable implements Configurable {

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    // AI Model Configuration Tab
    private JRadioButton autoSelectModelRadioButton;
    private JRadioButton geminiAiRadioButton;
    private JRadioButton openAiGpt4RadioButton;
    private JRadioButton claudeRadioButton;
    private JRadioButton ensembleModeRadioButton;
    private JBTextField geminiApiKeyField;
    private JBTextField openAiApiKeyField;
    private JBTextField claudeApiKeyField;
    private JBCheckBox storeKeysSecurelyCheckBox;
    private JBCheckBox validateKeysOnStartupCheckBox;

    // Analysis Configuration Tab
    private JRadioButton quickAnalysisRadioButton;
    private JRadioButton standardAnalysisRadioButton;
    private JRadioButton comprehensiveAnalysisRadioButton;
    private JBCheckBox securityVulnerabilitiesCheckBox;
    private JBCheckBox performanceIssuesCheckBox;
    private JBCheckBox codeSmellsCheckBox;
    private JBCheckBox documentationIssuesCheckBox;
    private JBCheckBox designPatternViolationsCheckBox;
    private JBCheckBox memoryResourceLeaksCheckBox;

    // Retaining existing for compatibility, though they might be deprecated
    private JBCheckBox enablePreCommitHookCheckBox;
    private JBTextField apiKeyField;


    private final AIReviewerSettingsState settings = AIReviewerSettingsState.getInstance();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AI Code Reviewer";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        tabbedPane = new JTabbedPane();

        // AI Model Configuration Tab
        geminiApiKeyField = new JBTextField();
        openAiApiKeyField = new JBTextField();
        claudeApiKeyField = new JBTextField();
        storeKeysSecurelyCheckBox = new JBCheckBox("Store keys securely (use IDE's password manager)");
        validateKeysOnStartupCheckBox = new JBCheckBox("Validate keys on startup");

        autoSelectModelRadioButton = new JRadioButton("Auto-Select (Recommended)");
        geminiAiRadioButton = new JRadioButton("Gemini AI");
        openAiGpt4RadioButton = new JRadioButton("OpenAI GPT-4");
        claudeRadioButton = new JRadioButton("Claude");
        ensembleModeRadioButton = new JRadioButton("Ensemble Mode (Experimental)");

        ButtonGroup modelButtonGroup = new ButtonGroup();
        modelButtonGroup.add(autoSelectModelRadioButton);
        modelButtonGroup.add(geminiAiRadioButton);
        modelButtonGroup.add(openAiGpt4RadioButton);
        modelButtonGroup.add(claudeRadioButton);
        modelButtonGroup.add(ensembleModeRadioButton);

        JPanel aiModelPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("Choose AI Model:"))
                .addComponent(autoSelectModelRadioButton)
                .addComponent(geminiAiRadioButton)
                .addComponent(openAiGpt4RadioButton)
                .addComponent(claudeRadioButton)
                .addComponent(ensembleModeRadioButton)
                .addSeparator()
                .addLabeledComponent(new JBLabel("Gemini API Key:"), geminiApiKeyField)
                .addLabeledComponent(new JBLabel("OpenAI API Key:"), openAiApiKeyField)
                .addLabeledComponent(new JBLabel("Claude API Key:"), claudeApiKeyField)
                .addComponentToRightColumn(new JButton("Test")) // Placeholder
                .addComponent(storeKeysSecurelyCheckBox)
                .addComponent(validateKeysOnStartupCheckBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        tabbedPane.addTab("AI Model Configuration", aiModelPanel);

        // Analysis Configuration Tab
        quickAnalysisRadioButton = new JRadioButton("Quick (fastest, surface-level)");
        standardAnalysisRadioButton = new JRadioButton("Standard (balanced speed and depth)");
        comprehensiveAnalysisRadioButton = new JRadioButton("Comprehensive (slowest, most thorough)");

        ButtonGroup analysisDepthGroup = new ButtonGroup();
        analysisDepthGroup.add(quickAnalysisRadioButton);
        analysisDepthGroup.add(standardAnalysisRadioButton);
        analysisDepthGroup.add(comprehensiveAnalysisRadioButton);

        securityVulnerabilitiesCheckBox = new JBCheckBox("Security Vulnerabilities");
        performanceIssuesCheckBox = new JBCheckBox("Performance Issues");
        codeSmellsCheckBox = new JBCheckBox("Code Smells");
        documentationIssuesCheckBox = new JBCheckBox("Documentation Issues");
        designPatternViolationsCheckBox = new JBCheckBox("Design Pattern Violations");
        memoryResourceLeaksCheckBox = new JBCheckBox("Memory & Resource Leaks");

        // Old settings for compatibility - might be removed later
        enablePreCommitHookCheckBox = new JBCheckBox("Enable AI review on pre-commit (Legacy)");
        apiKeyField = new JBTextField();


        JPanel analysisConfigPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("Select Analysis Depth:"))
                .addComponent(quickAnalysisRadioButton)
                .addComponent(standardAnalysisRadioButton)
                .addComponent(comprehensiveAnalysisRadioButton)
                .addSeparator()
                .addComponent(new JBLabel("Select Issue Categories to Scan For:"))
                .addComponent(securityVulnerabilitiesCheckBox)
                .addComponent(performanceIssuesCheckBox)
                .addComponent(codeSmellsCheckBox)
                .addComponent(documentationIssuesCheckBox)
                .addComponent(designPatternViolationsCheckBox)
                .addComponent(memoryResourceLeaksCheckBox)
                .addSeparator()
                .addComponent(enablePreCommitHookCheckBox) // Legacy
                .addLabeledComponent(new JBLabel("Legacy API Key (deprecated):"), apiKeyField, UIUtil.LARGE_VGAP, false) // Legacy
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        tabbedPane.addTab("Analysis Configuration", analysisConfigPanel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        if (settings == null) return false;
        boolean modified = false;

        // AI Model Tab
        modified |= !getSelectedModel().equals(settings.selectedModel);
        modified |= !geminiApiKeyField.getText().equals(settings.geminiApiKey);
        modified |= !openAiApiKeyField.getText().equals(settings.openAiApiKey);
        modified |= !claudeApiKeyField.getText().equals(settings.claudeApiKey);
        modified |= storeKeysSecurelyCheckBox.isSelected() != settings.storeKeysSecurely;
        modified |= validateKeysOnStartupCheckBox.isSelected() != settings.validateKeysOnStartup;

        // Analysis Configuration Tab
        modified |= !getSelectedAnalysisDepth().equals(settings.analysisDepth);
        modified |= securityVulnerabilitiesCheckBox.isSelected() != settings.issueCategories.getOrDefault("Security Vulnerabilities", false);
        modified |= performanceIssuesCheckBox.isSelected() != settings.issueCategories.getOrDefault("Performance Issues", false);
        modified |= codeSmellsCheckBox.isSelected() != settings.issueCategories.getOrDefault("Code Smells", false);
        modified |= documentationIssuesCheckBox.isSelected() != settings.issueCategories.getOrDefault("Documentation Issues", false);
        modified |= designPatternViolationsCheckBox.isSelected() != settings.issueCategories.getOrDefault("Design Pattern Violations", false);
        modified |= memoryResourceLeaksCheckBox.isSelected() != settings.issueCategories.getOrDefault("Memory & Resource Leaks", false);

        // Legacy fields
        modified |= enablePreCommitHookCheckBox.isSelected() != settings.preCommitHookEnabled;
        modified |= !apiKeyField.getText().equals(settings.apiKey);

        return modified;
    }

    @Override
    public void apply() {
        if (settings == null) return;

        // AI Model Tab
        settings.selectedModel = getSelectedModel();
        settings.geminiApiKey = geminiApiKeyField.getText();
        settings.openAiApiKey = openAiApiKeyField.getText();
        settings.claudeApiKey = claudeApiKeyField.getText();
        settings.storeKeysSecurely = storeKeysSecurelyCheckBox.isSelected();
        settings.validateKeysOnStartup = validateKeysOnStartupCheckBox.isSelected();

        // Analysis Configuration Tab
        settings.analysisDepth = getSelectedAnalysisDepth();
        settings.issueCategories.put("Security Vulnerabilities", securityVulnerabilitiesCheckBox.isSelected());
        settings.issueCategories.put("Performance Issues", performanceIssuesCheckBox.isSelected());
        settings.issueCategories.put("Code Smells", codeSmellsCheckBox.isSelected());
        settings.issueCategories.put("Documentation Issues", documentationIssuesCheckBox.isSelected());
        settings.issueCategories.put("Design Pattern Violations", designPatternViolationsCheckBox.isSelected());
        settings.issueCategories.put("Memory & Resource Leaks", memoryResourceLeaksCheckBox.isSelected());

        // Legacy fields
        settings.preCommitHookEnabled = enablePreCommitHookCheckBox.isSelected();
        settings.apiKey = apiKeyField.getText();
    }

    @Override
    public void reset() {
        if (settings == null) return;

        // AI Model Tab
        setSelectedModel(settings.selectedModel);
        geminiApiKeyField.setText(settings.geminiApiKey);
        openAiApiKeyField.setText(settings.openAiApiKey);
        claudeApiKeyField.setText(settings.claudeApiKey);
        storeKeysSecurelyCheckBox.setSelected(settings.storeKeysSecurely);
        validateKeysOnStartupCheckBox.setSelected(settings.validateKeysOnStartup);

        // Analysis Configuration Tab
        setSelectedAnalysisDepth(settings.analysisDepth);
        securityVulnerabilitiesCheckBox.setSelected(settings.issueCategories.getOrDefault("Security Vulnerabilities", true));
        performanceIssuesCheckBox.setSelected(settings.issueCategories.getOrDefault("Performance Issues", true));
        codeSmellsCheckBox.setSelected(settings.issueCategories.getOrDefault("Code Smells", true));
        documentationIssuesCheckBox.setSelected(settings.issueCategories.getOrDefault("Documentation Issues", false));
        designPatternViolationsCheckBox.setSelected(settings.issueCategories.getOrDefault("Design Pattern Violations", false));
        memoryResourceLeaksCheckBox.setSelected(settings.issueCategories.getOrDefault("Memory & Resource Leaks", true));

        // Legacy fields
        enablePreCommitHookCheckBox.setSelected(settings.preCommitHookEnabled);
        apiKeyField.setText(settings.apiKey);
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
        tabbedPane = null;
        autoSelectModelRadioButton = null;
        geminiAiRadioButton = null;
        openAiGpt4RadioButton = null;
        claudeRadioButton = null;
        ensembleModeRadioButton = null;
        geminiApiKeyField = null;
        openAiApiKeyField = null;
        claudeApiKeyField = null;
        storeKeysSecurelyCheckBox = null;
        validateKeysOnStartupCheckBox = null;
        quickAnalysisRadioButton = null;
        standardAnalysisRadioButton = null;
        comprehensiveAnalysisRadioButton = null;
        securityVulnerabilitiesCheckBox = null;
        performanceIssuesCheckBox = null;
        codeSmellsCheckBox = null;
        documentationIssuesCheckBox = null;
        designPatternViolationsCheckBox = null;
        memoryResourceLeaksCheckBox = null;
        enablePreCommitHookCheckBox = null;
        apiKeyField = null;
    }

    private String getSelectedModel() {
        if (geminiAiRadioButton.isSelected()) return "Gemini AI";
        if (openAiGpt4RadioButton.isSelected()) return "OpenAI GPT-4";
        if (claudeRadioButton.isSelected()) return "Claude";
        if (ensembleModeRadioButton.isSelected()) return "Ensemble Mode";
        return "Auto-Select"; // Default
    }

    private void setSelectedModel(String model) {
        if ("Gemini AI".equals(model)) geminiAiRadioButton.setSelected(true);
        else if ("OpenAI GPT-4".equals(model)) openAiGpt4RadioButton.setSelected(true);
        else if ("Claude".equals(model)) claudeRadioButton.setSelected(true);
        else if ("Ensemble Mode".equals(model)) ensembleModeRadioButton.setSelected(true);
        else autoSelectModelRadioButton.setSelected(true);
    }

    private String getSelectedAnalysisDepth() {
        if (quickAnalysisRadioButton.isSelected()) return "Quick";
        if (comprehensiveAnalysisRadioButton.isSelected()) return "Comprehensive";
        return "Standard"; // Default
    }

    private void setSelectedAnalysisDepth(String depth) {
        if ("Quick".equals(depth)) quickAnalysisRadioButton.setSelected(true);
        else if ("Comprehensive".equals(depth)) comprehensiveAnalysisRadioButton.setSelected(true);
        else standardAnalysisRadioButton.setSelected(true);
    }
}
