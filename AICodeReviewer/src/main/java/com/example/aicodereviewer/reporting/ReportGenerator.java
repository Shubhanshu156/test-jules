package com.example.aicodereviewer.reporting;

import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    private static final Logger LOG = Logger.getInstance(ReportGenerator.class);
    private static final String REPORT_DIR_NAME = ".review";
    private static final String NOTIFICATION_GROUP_ID = "AI_CODE_REVIEWER_NOTIFICATIONS";

    public static void saveReportAsJson(Project project, AIReviewResponse reviewResponse) {
        if (project == null || reviewResponse == null) {
            LOG.warn("Project or ReviewResponse is null, cannot save report.");
            return;
        }

        String projectBasePath = project.getBasePath();
        if (projectBasePath == null) {
            LOG.warn("Project base path is null, cannot save report.");
            return;
        }

        try {
            // Define and create the report directory
            Path reportDirPath = Paths.get(projectBasePath, REPORT_DIR_NAME);
            if (!Files.exists(reportDirPath)) {
                Files.createDirectories(reportDirPath);
                LOG.info("Created report directory: " + reportDirPath);
            }

            // Generate filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "review_report_" + timestamp + ".json";
            File reportFile = new File(reportDirPath.toFile(), filename);

            // Serialize response to JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonReport = gson.toJson(reviewResponse);

            // Write to file
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(jsonReport);
                LOG.info("AI review report saved to: " + reportFile.getAbsolutePath());

                // Notify user - with null safety
                showNotification(project, "AI Review Report Generated",
                        "Report saved to: " + reportFile.getAbsolutePath(), NotificationType.INFORMATION);
            }

        } catch (IOException e) {
            LOG.error("Error saving AI review report: " + e.getMessage(), e);
            showNotification(project, "AI Review Report Failed",
                    "Could not save report: " + e.getMessage(), NotificationType.ERROR);
        } catch (Exception e) {
            LOG.error("An unexpected error occurred while saving AI review report: " + e.getMessage(), e);
            showNotification(project, "AI Review Report Failed",
                    "Unexpected error saving report: " + e.getMessage(), NotificationType.ERROR);
        }
    }

    private static void showNotification(Project project, String title, String content, NotificationType type) {
        try {
            NotificationGroup notificationGroup = NotificationGroupManager.getInstance()
                    .getNotificationGroup(NOTIFICATION_GROUP_ID);

            if (notificationGroup != null) {
                notificationGroup.createNotification(title, content, type).notify(project);
            } else {
                // Fallback: Log the message if notification group is not available
                LOG.info("Notification: " + title + " - " + content);
            }
        } catch (Exception e) {
            LOG.warn("Failed to show notification: " + e.getMessage() + ". Message was: " + title + " - " + content);
        }
    }
}