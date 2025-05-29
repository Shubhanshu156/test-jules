package com.example.aicodereviewer.ui;

import com.example.aicodereviewer.ai.model.AIReviewIssue;
import com.example.aicodereviewer.ai.model.IssueSeverity; // For direct access if needed

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class IssuesTableModel extends AbstractTableModel {

    private final List<AIReviewIssue> issues;
    private final String[] columnNames = {"Severity", "File", "Line", "Message"};

    public IssuesTableModel(List<AIReviewIssue> issues) {
        this.issues = issues;
    }

    @Override
    public int getRowCount() {
        return issues.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AIReviewIssue issue = issues.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return issue.getSeverity();
            case 1:
                // Show only the filename, not the full path, for brevity in table
                String filePath = issue.getFilePath();
                return filePath.substring(filePath.lastIndexOf('/') + 1);
            case 2:
                return issue.getLineStart();
            case 3:
                return issue.getMessage();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return IssueSeverity.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return String.class;
            default:
                return Object.class;
        }
    }

    public AIReviewIssue getIssueAt(int rowIndex) {
        return issues.get(rowIndex);
    }
}
