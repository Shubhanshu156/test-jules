package com.example.aicodereviewer.ai.model;

import java.util.Map;

public class AIModelInfo {

    private String modelName;
    private String modelVersion;
    private String provider;
    private Map<String, Object> capabilities;

    public AIModelInfo(String modelName, String modelVersion, String provider, Map<String, Object> capabilities) {
        this.modelName = modelName;
        this.modelVersion = modelVersion;
        this.provider = provider;
        this.capabilities = capabilities;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
}
