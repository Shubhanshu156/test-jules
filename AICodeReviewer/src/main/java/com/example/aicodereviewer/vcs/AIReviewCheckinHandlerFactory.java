package com.example.aicodereviewer.vcs;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.CommitMessage; // For CommitContext, though not directly used in this simple factory
import org.jetbrains.annotations.NotNull;

public class AIReviewCheckinHandlerFactory extends CheckinHandlerFactory {

    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new AIReviewCheckinHandler(panel.getProject(), panel);
    }
}
