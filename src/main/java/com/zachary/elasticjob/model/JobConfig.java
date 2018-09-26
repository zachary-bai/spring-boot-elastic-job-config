package com.zachary.elasticjob.model;

import java.util.List;

/**
 * @author zachary
 */
public final class JobConfig {

    private String jobClass;
    private String cron;
    private int shardingTotalCount;
    private String shardingItemParameters;
    private String jobParameter;
    private boolean failover;
    private boolean misfire;
    private String description;
    private List<String> listeners;
    private boolean disabled;

    private boolean streamingProcess;
    private String scriptCommandLine;

    public JobConfig() {
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getShardingTotalCount() {
        return shardingTotalCount;
    }

    public void setShardingTotalCount(int shardingTotalCount) {
        this.shardingTotalCount = shardingTotalCount;
    }

    public String getShardingItemParameters() {
        return shardingItemParameters;
    }

    public void setShardingItemParameters(String shardingItemParameters) {
        this.shardingItemParameters = shardingItemParameters;
    }

    public String getJobParameter() {
        return jobParameter;
    }

    public void setJobParameter(String jobParameter) {
        this.jobParameter = jobParameter;
    }

    public boolean isFailover() {
        return failover;
    }

    public void setFailover(boolean failover) {
        this.failover = failover;
    }

    public boolean isMisfire() {
        return misfire;
    }

    public void setMisfire(boolean misfire) {
        this.misfire = misfire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStreamingProcess() {
        return streamingProcess;
    }

    public List<String> getListeners() {
        return listeners;
    }

    public void setListeners(List<String> listeners) {
        this.listeners = listeners;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setStreamingProcess(boolean streamingProcess) {
        this.streamingProcess = streamingProcess;
    }

    public String getScriptCommandLine() {
        return scriptCommandLine;
    }

    public void setScriptCommandLine(String scriptCommandLine) {
        this.scriptCommandLine = scriptCommandLine;
    }
}