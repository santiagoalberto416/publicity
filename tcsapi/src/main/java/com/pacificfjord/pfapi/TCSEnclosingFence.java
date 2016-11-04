package com.pacificfjord.pfapi;

import java.util.Date;

/**
 * Created by tom on 16/01/14.
 */
public class TCSEnclosingFence {

    private boolean valid = false;
    private double minEnclosingTime;
    private String fenceId;
    private Date entryDateTime;
    private Date lastReEntryDateTime;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setMinEnclosingTime(double minEnclosingTime) {
        this.minEnclosingTime = minEnclosingTime;
    }

    public double getMinEnclosingTime() {
        return minEnclosingTime;
    }

    public String getFenceId() {
        return fenceId;
    }

    public Date getEntryDateTime() {
        return entryDateTime;
    }

    public Date getLastReEntryDateTime() {
        return lastReEntryDateTime;
    }

    public void setLastReEntryDateTime(Date lastReEntryDateTime) {
        this.lastReEntryDateTime = lastReEntryDateTime;
    }

    public TCSEnclosingFence(String UID, Date entryTime, double minEnclosingTime)
    {
        valid = true;
        entryDateTime = entryTime;
        lastReEntryDateTime = entryDateTime;
        fenceId = UID;
        this.minEnclosingTime = minEnclosingTime;
    }

    public boolean testExitTime(Date exitEventTime)
    {
        long timeDifference = exitEventTime.getTime() - lastReEntryDateTime.getTime();

        return (double)timeDifference > minEnclosingTime;
    }
}
