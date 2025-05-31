package com.example.aicodereviewer.ai;

public class AIProviderException extends Exception {
    public enum ErrorType {
        AUTHENTICATION_FAILED, RATE_LIMIT_EXCEEDED, 
        NETWORK_ERROR, INVALID_REQUEST, SERVICE_UNAVAILABLE
    }
    private final ErrorType errorType;

    public AIProviderException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    public AIProviderException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
    public ErrorType getErrorType() {
        return errorType;
    }
} 