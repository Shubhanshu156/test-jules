package com.example.aicodereviewer.ai;

import com.example.aicodereviewer.ai.model.AIModelInfo;
import com.example.aicodereviewer.ai.model.AIReviewRequest;
import com.example.aicodereviewer.ai.model.AIReviewResponse;
// No need to import AIReviewIssue or IssueType directly unless used in this class's logic beyond passthrough

public class CompositeAIService implements AICodeReviewService {

    private final AICodeReviewService primaryService;
    private final AICodeReviewService secondaryService;

    public CompositeAIService(AICodeReviewService primaryService, AICodeReviewService secondaryService) {
        if (primaryService == null) {
            throw new IllegalArgumentException("Primary service cannot be null.");
        }
        if (secondaryService == null) {
            throw new IllegalArgumentException("Secondary service cannot be null.");
        }
        this.primaryService = primaryService;
        this.secondaryService = secondaryService;
    }

    @Override
    public AIReviewResponse analyzeCode(AIReviewRequest request) {
        AIReviewResponse response;
        try {
            System.out.println("CompositeAIService: Attempting to use primary service: " + primaryService.getModelInfo().getModelName());
            response = primaryService.analyzeCode(request);
            if (response != null && response.isSuccess()) {
                System.out.println("CompositeAIService: Primary service succeeded.");
                return response;
            } else {
                System.out.println("CompositeAIService: Primary service failed or returned unsuccessful response. Falling back to secondary service.");
                // Log details of primary failure if possible, e.g., response.getErrorMessage()
                if (response != null && response.getErrorMesage() != null && !response.getErrorMesage().isEmpty()) {
                    System.err.println("Primary service error message: " + response.getErrorMesage());
                }
            }
        } catch (Exception e) {
            System.err.println("CompositeAIService: Exception from primary service '" + primaryService.getModelInfo().getModelName() + "': " + e.getMessage());
            System.out.println("CompositeAIService: Falling back to secondary service: " + secondaryService.getModelInfo().getModelName());
        }
        
        // If primary failed (threw exception or response not successful), try secondary
        System.out.println("CompositeAIService: Attempting to use secondary service: " + secondaryService.getModelInfo().getModelName());
        response = secondaryService.analyzeCode(request); // If this also fails, its response/exception will propagate
        if (response != null && response.isSuccess()){
             System.out.println("CompositeAIService: Secondary service succeeded.");
        } else {
            System.out.println("CompositeAIService: Secondary service also failed or returned unsuccessful response.");
             if (response != null && response.getErrorMesage() != null && !response.getErrorMesage().isEmpty()) {
                System.err.println("Secondary service error message: " + response.getErrorMesage());
            }
        }
        return response;
    }

    @Override
    public boolean isHealthy() {
        // Healthy if the primary service is healthy.
        // Could be extended: return primaryService.isHealthy() || secondaryService.isHealthy(); 
        // if healthy means at least one can respond.
        boolean primaryHealthy = primaryService.isHealthy();
        System.out.println("CompositeAIService: Primary service health: " + primaryHealthy);
        return primaryHealthy;
    }

    @Override
    public AIModelInfo getModelInfo() {
        // Returns the model info of the primary service.
        // Could be customized to indicate a composite model.
        return primaryService.getModelInfo();
    }
}
