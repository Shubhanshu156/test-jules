# AI Code Reviewer

## Description

An IntelliJ IDEA plugin designed to provide AI-powered code review for improving code quality, style, and identifying potential bugs.
This plugin is currently in an early proof-of-concept stage, with many analysis features simulated by a mock AI service to demonstrate intended capabilities.

## Current Features (Proof of Concept)

The plugin currently offers the following features:

*   **Pre-commit Code Review Hook:**
    *   Automatically reviews changed files when you attempt to commit them via Git.
    *   This review is performed by a *mock* AI service that simulates various checks.
    *   **Critical issues** (e.g., finding "FIXME" comments) will block the commit, prompting you to review the problems. You can choose to commit anyway via IntelliJ IDEA's default dialog.

*   **Configurable Mock Analysis Checks:**
    *   The pre-commit review (and other future analysis actions) can (conceptually) run specific checks. The mock service currently simulates:
        *   **Basic Indentation Issues:** Detects obviously inconsistent leading whitespace (simulated).
        *   **Unused Imports:** Identifies import statements that don't appear to be used in the file (simulated).
        *   **Basic Low-Level Design (LLD) Principles:**
            *   **Method Too Long:** Flags methods or code blocks that exceed a defined line count (e.g., >30 lines, simulated).
            *   **Method with Too Many Parameters:** Flags method signatures that appear to have too many parameters (e.g., >5 parameters, simulated).
        *   Other general checks like presence of "FIXME" (Critical), Kotlin `var` usage suggestions, and long line suggestions are also part of the mock analysis.

*   **Interactive Review Results Dialog:**
    *   When issues are found during the pre-commit review, a dialog box appears titled "AI Code Review Results".
    *   This dialog displays a summary of findings (counts of critical issues, warnings, suggestions).
    *   A sortable table lists individual issues with details: Severity, File, Line, and Message.
    *   **Go to Source:** You can double-click an issue in the table or select an issue and click the "Go to Source" button to navigate directly to the relevant line in the source file.

*   **JSON Report Generation:**
    *   After a pre-commit review that finds issues, a JSON report detailing the `AIReviewResponse` (including all issues and summary) is automatically saved.
    *   These reports are stored in a `.review` directory created at the root of your project.
    *   Filenames are timestamped (e.g., `review_report_yyyyMMdd_HHmmss.json`).

*   **Plugin Settings Panel:**
    *   A basic settings panel is available under `File -> Settings/Preferences -> Tools -> AI Code Reviewer`.
    *   **Enable/Disable Pre-commit Hook:** You can toggle the automatic pre-commit review on or off.
    *   **API Key (Placeholder):** An API key field is present as a placeholder for future integration with real AI services. It is not currently used.

*   **Manual Analysis Action (Basic):**
    *   An action available via `Tools -> Analyze Code with Mock AI`.
    *   This action takes the content of the currently active text editor, sends it to the mock AI service, and displays the *full simulated response summary* as an IDE notification.

*   **Test Notification Action:**
    *   A simple action (`Tools -> Show AI Reviewer Notification`) to display a test notification, confirming the plugin is active.

## Configuration

To configure the AI Code Reviewer plugin:

1.  Open IntelliJ IDEA settings:
    *   **Windows/Linux:** `File -> Settings...`
    *   **macOS:** `IntelliJ IDEA -> Preferences...` (or `Settings...` in newer versions)
2.  Navigate to **Tools -> AI Code Reviewer**.
3.  Here you can:
    *   **Enable AI review on pre-commit:** Check or uncheck this box to control the automatic review process during commits.
    *   View the placeholder API Key field.

## How to Build/Run from Source

**Prerequisites:**

*   IntelliJ IDEA (Community or Ultimate Edition).
*   JDK 17 (as configured in the project's `build.gradle.kts`). You can typically set this up under `File -> Project Structure -> SDKs`.

**Steps:**

1.  **Clone the Repository:**
    ```bash
    # git clone <repository_url> 
    # cd AICodeReviewer
    ```
    (Or open the project directly if you have the files.)
2.  **Open in IntelliJ IDEA:** Open the `AICodeReviewer` directory as a project in IntelliJ IDEA.
3.  **Gradle Sync:**
    *   Ensure the Gradle plugin is enabled in IntelliJ IDEA (`File -> Settings/Preferences -> Plugins -> Installed -> search for "Gradle"`).
    *   IntelliJ IDEA should automatically detect it as a Gradle project. If prompted, or if dependencies are not immediately resolved, sync the Gradle project. You can usually find a "Sync" or "Reload" button in the Gradle tool window (`View -> Tool Windows -> Gradle`).
4.  **Run the Plugin:**
    *   Open the Gradle tool window (`View -> Tool Windows -> Gradle`).
    *   Navigate to `AICodeReviewer -> Tasks -> intellij -> runIde`.
    *   Double-click `runIde` to build the plugin and launch a new (sandboxed) instance of IntelliJ IDEA with the "AI Code Reviewer" plugin installed and active.
    *   Inside the new IntelliJ IDEA instance, you can test the features described above.

## Future Goals (Briefly)

*   Integrate with actual Large Language Models (LLMs) or other AI services for genuine code analysis.
*   Replace mock analysis heuristics with proper PSI-based code analysis for accuracy.
*   Enhance the UI/UX for displaying suggestions (e.g., editor annotations, a dedicated tool window for issues).
*   Allow configuration of specific rules and thresholds in the settings panel.
*   Support real-time or on-demand analysis of selected code, entire files, or projects.
*   Provide more detailed explanations and suggested fixes for identified issues.

---
*This README reflects the project's state, including features developed through a guided process.*
