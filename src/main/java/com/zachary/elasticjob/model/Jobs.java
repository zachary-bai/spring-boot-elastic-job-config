package com.zachary.elasticjob.model;

import java.util.List;

/**
 * @author zachary
 */
public class Jobs {

    private List<JobConfig> jobs;

    public Jobs() {
    }

    public List<JobConfig> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobConfig> jobs) {
        this.jobs = jobs;
    }
}
