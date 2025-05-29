package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockAIService {

    private static final MockAIService INSTANCE = new MockAIService();

    private static final int MAX_CACHE_SIZE = 10;
    private final Map<String, AIReviewResponse> responseCache = new LinkedHashMap<String, AIReviewResponse>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, AIReviewResponse> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private final Random random = new Random();
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^import\\s+([\\w.]+);?", Pattern.MULTILINE);
    // Pattern to find potential method signatures (very basic)
    private static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile(
        "(public|protected|private|static|final|synchronized|abstract|native|strictfp)?\\s*" + // Modifiers
        "([\\w<>,\\[\\]]+(?:\\s*\\[\\])?)\\s+" +  // Return type (including generics, arrays)
        "([a-zA-Z_][\\w]*)\\s*" +             // Method name
        "\\(([^)]*)\\)" +                     // Parameters within parentheses
        "\\s*(throws\\s+[\\w,.\\s]+)?\\s*\\{", // Optional throws clause and opening brace
        Pattern.MULTILINE
    );
    private static final int MAX_METHOD_LINES = 30;
    private static final int MAX_PARAMETERS = 5;


    private MockAIService() {}

    public static MockAIService getInstance() {
        return INSTANCE;
    }

    public AIReviewResponse analyzeCode(AIReviewRequest request) {
        String cacheKey = request.getCacheKey();
        AIReviewPreferences preferences = request.getReviewPreferences() == null ? new AIReviewPreferences() : request.getReviewPreferences();
        Set<IssueType> enabledChecks = preferences.getEnabledChecks() == null ? Set.of() : preferences.getEnabledChecks();

        if (responseCache.containsKey(cacheKey)) {
            AIReviewResponse cachedResponse = responseCache.get(cacheKey);
            cachedResponse.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
            try { TimeUnit.MILLISECONDS.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return cachedResponse;
        }

        long startTime = System.currentTimeMillis();
        List<AIReviewIssue> issues = new ArrayList<>();
        AIReviewSummary summary = new AIReviewSummary();

        if (request.getAnalyzeTarget() != null && request.getAnalyzeTarget().getFiles() != null) {
            summary.setTotalFilesAnalyzed(request.getAnalyzeTarget().getFiles().size());
            for (AIReviewFile file : request.getAnalyzeTarget().getFiles()) {
                String content = file.getContent() != null ? file.getContent() : "";
                String filePath = file.getFilePath();
                String[] lines = content.split("\n");

                // Existing "always-on" mock rules
                applyAlwaysOnRules(filePath, content, lines, issues, summary);

                // Conditional LLD Checks
                if (enabledChecks.contains(IssueType.LLD_METHOD_TOO_LONG)) {
                    findLongMethods(filePath, lines, issues, summary);
                }
                if (enabledChecks.contains(IssueType.LLD_TOO_MANY_PARAMETERS)) {
                    findMethodsWithTooManyParameters(filePath, lines, issues, summary);
                }
                
                // Conditional Unused Import and Indentation
                if (enabledChecks.contains(IssueType.UNUSED_IMPORT)) {
                    findUnusedImports(filePath, content, lines, issues, summary);
                }
                if (enabledChecks.contains(IssueType.INDENTATION)) {
                    checkIndentation(filePath, lines, issues, summary);
                }
            }
        }

        try { TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(200)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // Reduced overall delay slightly

        long endTime = System.currentTimeMillis();
        summary.setAnalysisDurationMs(endTime - startTime);
        AIReviewResponse response = new AIReviewResponse(request.getRequestId(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()), summary, issues);
        responseCache.put(cacheKey, response);
        return response;
    }

    private void applyAlwaysOnRules(String filePath, String content, String[] lines, List<AIReviewIssue> issues, AIReviewSummary summary) {
        // Rule 1: FIXME keyword
        if (content.contains("FIXME")) {
            issues.add(new AIReviewIssue(filePath, findLineNumber(lines, "FIXME"), null, IssueType.BUG, IssueSeverity.CRITICAL,
                    "Critical: Found 'FIXME' keyword. This requires immediate attention.", "The 'FIXME' comment indicates an incomplete or problematic section of code.", "Address the FIXME comment and resolve the underlying issue."));
            summary.incrementCritical();
        }
        // Rule 2: Kotlin 'var' usage
        if (filePath.endsWith(".kt") && content.contains("var ")) {
             if ((content.split("var ", -1).length - 1) > 1) {
                issues.add(new AIReviewIssue(filePath, findLineNumber(lines, "var "), null, IssueType.STYLE, IssueSeverity.SUGGESTION,
                        "Suggestion: Consider using 'val' for immutable variables in Kotlin where possible.", "Using 'val' promotes immutability, making code safer and easier to reason about.", "Replace 'var' with 'val' if the variable is not reassigned after initialization."));
                summary.incrementSuggestion();
             }
        }
        // Rule 4: Long lines
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > 100) {
                 issues.add(new AIReviewIssue(filePath, i + 1, null, IssueType.STYLE, IssueSeverity.SUGGESTION,
                        "Suggestion: Line " + (i+1) + " exceeds 100 characters. Consider shortening it.", "Long lines can be hard to read and may not display well on all screens.", "Break the line into multiple shorter lines."));
                summary.incrementSuggestion();
                break; 
            }
        }
    }

    private void findLongMethods(String filePath, String[] lines, List<AIReviewIssue> issues, AIReviewSummary summary) {
        int currentLine = 0;
        while (currentLine < lines.length) {
            Matcher methodMatcher = METHOD_SIGNATURE_PATTERN.matcher(lines[currentLine]);
            String currentMethodName = "UnknownMethod";
            if (methodMatcher.lookingAt()) { // Use lookingAt() to match from the beginning of the line
                 currentMethodName = methodMatcher.group(3) != null ? methodMatcher.group(3) : currentMethodName;
            }

            if (lines[currentLine].contains("{")) { // A simplistic way to find a block start
                int blockStartLine = currentLine + 1;
                int braceCount = 0;
                for (char c : lines[currentLine].toCharArray()) if (c == '{') braceCount++;

                int lineInBlock = 0;
                for (int i = currentLine + 1; i < lines.length; i++) {
                    for (char c : lines[i].toCharArray()) {
                        if (c == '{') braceCount++;
                        if (c == '}') braceCount--;
                    }
                    lineInBlock++;
                    if (braceCount == 0) { // End of block
                        if (lineInBlock > MAX_METHOD_LINES) {
                            issues.add(new AIReviewIssue(filePath, blockStartLine, null, IssueType.LLD_METHOD_TOO_LONG, IssueSeverity.WARNING,
                                    "Method '" + currentMethodName + "' appears too long (" + lineInBlock + " lines). Max allowed: " + MAX_METHOD_LINES,
                                    "Long methods are hard to understand and maintain. They often indicate too many responsibilities.",
                                    "Consider refactoring the method into smaller, more focused methods."));
                            summary.incrementWarning();
                        }
                        currentLine = i + 1;
                        break;
                    }
                    if (i == lines.length - 1) { // Reached end of file before block ended
                        currentLine = lines.length;
                    }
                }
                 if (braceCount != 0 && lineInBlock > MAX_METHOD_LINES) { // Block didn't close but is already too long
                     issues.add(new AIReviewIssue(filePath, blockStartLine, null, IssueType.LLD_METHOD_TOO_LONG, IssueSeverity.WARNING,
                                    "Code block (possibly method '" + currentMethodName + "') appears too long (" + lineInBlock + " lines) and might be unclosed. Max allowed: " + MAX_METHOD_LINES,
                                    "Long code blocks are hard to understand and maintain.",
                                    "Ensure the block is properly closed and consider refactoring."));
                            summary.incrementWarning();
                 }


            } else {
                currentLine++;
            }
        }
    }

    private void findMethodsWithTooManyParameters(String filePath, String[] lines, List<AIReviewIssue> issues, AIReviewSummary summary) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            Matcher matcher = METHOD_SIGNATURE_PATTERN.matcher(line);
            if (matcher.matches()) { // Use matches() to ensure the whole line fits the pattern for a signature
                String methodName = matcher.group(3);
                String paramsString = matcher.group(4);
                if (paramsString != null && !paramsString.trim().isEmpty()) {
                    String[] params = paramsString.split(",");
                    if (params.length > MAX_PARAMETERS) {
                        issues.add(new AIReviewIssue(filePath, i + 1, null, IssueType.LLD_TOO_MANY_PARAMETERS, IssueSeverity.WARNING,
                                "Method '" + methodName + "' has too many parameters (" + params.length + "). Max allowed: " + MAX_PARAMETERS,
                                "Methods with many parameters can be difficult to use and test. It might indicate that the method is doing too much or that parameters should be grouped.",
                                "Consider grouping parameters into a dedicated object/class, or refactoring the method."));
                        summary.incrementWarning();
                    }
                }
            }
        }
    }
    
    private void findUnusedImports(String filePath, String content, String[] lines, List<AIReviewIssue> issues, AIReviewSummary summary){
        Matcher importMatcher = IMPORT_PATTERN.matcher(content);
        List<Pair<String, Integer>> importStatements = new ArrayList<>();
        while(importMatcher.find()){
            importStatements.add(new Pair<>(importMatcher.group(1), findLineNumber(lines, importMatcher.group(0))));
        }
        String contentWithoutImports = importMatcher.replaceAll("");
        for(Pair<String, Integer> importEntry : importStatements){
            String importIdentifier = importEntry.first.substring(importEntry.first.lastIndexOf('.') + 1);
            if (importIdentifier.equals("*")) continue;
            Pattern usagePattern = Pattern.compile("\\b" + Pattern.quote(importIdentifier) + "\\b");
            if (!usagePattern.matcher(contentWithoutImports).find()) {
                issues.add(new AIReviewIssue(filePath, importEntry.second, null, IssueType.UNUSED_IMPORT, IssueSeverity.WARNING,
                        "Unused import: " + importEntry.first, "This import statement does not seem to be used in the file.", "Remove the unused import statement."));
                summary.incrementWarning();
            }
        }
    }

    private void checkIndentation(String filePath, String[] lines, List<AIReviewIssue> issues, AIReviewSummary summary){
        int previousIndent = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty()) continue;
            int currentIndent = 0;
            while (currentIndent < line.length() && (line.charAt(currentIndent) == ' ' || line.charAt(currentIndent) == '\t')) currentIndent++;
            if (previousIndent != -1 && currentIndent != previousIndent && Math.abs(currentIndent - previousIndent) % 2 != 0 && Math.abs(currentIndent - previousIndent) % 4 != 0 && currentIndent > 0) {
                if (currentIndent < previousIndent && (previousIndent - currentIndent) < 8 && (previousIndent - currentIndent) > 0 ) {} else {
                    issues.add(new AIReviewIssue(filePath, i + 1, null, IssueType.INDENTATION, IssueSeverity.WARNING,
                            "Warning: Potential inconsistent indentation detected around line " + (i + 1) +". Previous indent: " + previousIndent + ", current: " + currentIndent,
                            "Consistent indentation improves code readability and maintainability.","Ensure code follows a consistent indentation style."));
                    summary.incrementWarning();
                    break; 
                }
            }
            if (!line.trim().startsWith("}") && !line.trim().startsWith(")") && !line.trim().startsWith("</")) {
               previousIndent = currentIndent;
            }
        }
    }

    private int findLineNumber(String[] lines, String keyword) {
        if (lines == null || keyword == null) return 1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(keyword)) return i + 1;
        }
        return 1;
    }
    
    private static class Pair<F, S> {
        public final F first; public final S second;
        public Pair(F first, S second) { this.first = first; this.second = second; }
    }
}
