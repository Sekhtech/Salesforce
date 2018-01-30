package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 14-May-17.
 */

public class job_model {
    private String start;
    private String end;
    private String job;
    private String subJob;
    private String duration;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    private int durationInMins;

    public job_model(String start, String end, String job, String subJob, String duration, String date, int durationInMins) {
        this.start = start;
        this.end = end;
        this.job = job;
        this.subJob = subJob;
        this.duration = duration;
        this.date = date;
        this.durationInMins = durationInMins;
    }

    public String getStart() {

        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSubJob() {
        return subJob;
    }

    public void setSubJob(String subJob) {
        this.subJob = subJob;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDurationInMins() {
        return durationInMins;
    }

    public void setDurationInMins(int durationInMins) {
        this.durationInMins = durationInMins;
    }

    public job_model() {

    }
}
