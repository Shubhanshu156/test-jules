package com.example.aicodereviewer.reporting;

import com.example.aicodereviewer.ai.model.AIReviewResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
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
    private static final String NOTIFICATION_GROUP = "AI Code Reviewer Report Notification";

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

                // Notify user
                NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP)
                        .createNotification("AI Review Report Generated", "Report saved to: " + reportFile.getAbsolutePath(), NotificationType.INFORMATION)
                        .notify(project);
            }

        } catch (IOException e) {
            LOG.error("Error saving AI review report: " + e.getMessage(), e);
            NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP)
                        .createNotification("AI Review Report Failed", "Could not save report: " + e.getMessage(), NotificationType.ERROR)
                        .notify(project);
        } catch (Exception e) { // Catch other potential errors like SecurityException from createDirectories
            LOG.error("An unexpected error occurred while saving AI review report: " + e.getMessage(), e);
             NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP)
                        .createNotification("AI Review Report Failed", "Unexpected error saving report: " + e.getMessage(), NotificationType.ERROR)
                        .notify(project);
        }
    }
}
