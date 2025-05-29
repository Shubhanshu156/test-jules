package com.example.aicodereviewer.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AIReviewerSettingsConfigurable implements Configurable {

    private JPanel mainPanel;
    private JBCheckBox enablePreCommitHookCheckBox;
    private JBTextField apiKeyField;

    // Helper to access settings state
    private final AIReviewerSettingsState settings = AIReviewerSettingsState.getInstance();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AI Code Reviewer";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        enablePreCommitHookCheckBox = new JBCheckBox("Enable AI review on pre-commit");
        apiKeyField = new JBTextField();
        // Consider using JBPasswordField if this were a real API key

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(enablePreCommitHookCheckBox)
                .addLabeledComponent(new JBLabel("API Key (placeholder):"), apiKeyField, UIUtil.LARGE_VGAP, false)
                .addComponentFillVertically(new JPanel(), 0) // Spacer
                .getPanel();
        
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        if (settings == null) return false;
        boolean modified = enablePreCommitHookCheckBox.isSelected() != settings.preCommitHookEnabled;
        modified |= !apiKeyField.getText().equals(settings.apiKey);
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settings == null) return;
        settings.preCommitHookEnabled = enablePreCommitHookCheckBox.isSelected();
        settings.apiKey = apiKeyField.getText();
        // In a real scenario, you might want to validate the API key or other settings here
    }

    @Override
    public void reset() {
        if (settings == null) return;
        enablePreCommitHookCheckBox.setSelected(settings.preCommitHookEnabled);
        apiKeyField.setText(settings.apiKey);
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
        enablePreCommitHookCheckBox = null;
        apiKeyField = null;
    }
}
