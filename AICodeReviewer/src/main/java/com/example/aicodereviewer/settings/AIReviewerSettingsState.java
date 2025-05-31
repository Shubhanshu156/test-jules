package com.example.aicodereviewer.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "com.example.aicodereviewer.settings.AIReviewerSettingsState",
    storages = @Storage("AIReviewerSettings.xml")
)
public class AIReviewerSettingsState implements PersistentStateComponent<AIReviewerSettingsState> {

    public String selectedModel = "Auto-Select";
    public String geminiApiKey = "";
    public String openAiApiKey = "";
    public String claudeApiKey = "";
    public boolean storeKeysSecurely = true;
    public boolean validateKeysOnStartup = true;
    public String analysisDepth = "Standard";
    public Map<String, Boolean> issueCategories = new HashMap<>();

    public boolean preCommitHookEnabled = true; // Retaining existing fields
    public String apiKey = ""; // Retaining existing fields, though it might be deprecated by specific keys

    public AIReviewerSettingsState() {
        // Initialize default issue categories
        issueCategories.put("Security Vulnerabilities", true);
        issueCategories.put("Performance Issues", true);
        issueCategories.put("Code Smells", true);
        issueCategories.put("Documentation Issues", false);
        issueCategories.put("Design Pattern Violations", false);
        issueCategories.put("Memory & Resource Leaks", true);
    }

    public static AIReviewerSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AIReviewerSettingsState.class);
    }

    @Nullable
    @Override
    public AIReviewerSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AIReviewerSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
