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

    public boolean preCommitHookEnabled = true;
    public String apiKey = "";

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
