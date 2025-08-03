package com.example.long_short_polling.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// EC2Instance.java - Model for EC2 instance
public class EC2Instance {
    private String instanceId;
    private InstanceStatus status;
    private long createdAt;

    public enum InstanceStatus {
        PENDING, RUNNING, STOPPED, TERMINATED
    }

    public EC2Instance() {
        // Default constructor for Jackson
    }

    public EC2Instance(String instanceId) {
        this.instanceId = instanceId;
        this.status = InstanceStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    @JsonCreator
    public EC2Instance(@JsonProperty("instanceId") String instanceId, 
                      @JsonProperty("status") InstanceStatus status, 
                      @JsonProperty("createdAt") long createdAt) {
        this.instanceId = instanceId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getInstanceId() { return instanceId; }
    public InstanceStatus getStatus() { return status; }
    public void setStatus(InstanceStatus status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
