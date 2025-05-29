package com.example.aicodereviewer;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ShowNotificationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        NotificationGroupManager.getInstance()
                .getNotificationGroup("AI Code Reviewer Notification Group")
                .createNotification("AI Code Reviewer", "Plugin action executed successfully!", NotificationType.INFORMATION)
                .notify(project);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Make sure the action is always available and enabled.
        // This is suitable for a simple test action.
        // For real actions, you might want to check for project context, selected editor, etc.
        e.getPresentation().setEnabledAndVisible(true);
    }
}
