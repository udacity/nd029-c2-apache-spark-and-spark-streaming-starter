package com.getsimplex.steptimer.model;

public class Tail {

    private Long milliseconds;
    private String currentReading;
    private String macIdOfUnit;
    private String sessionId;

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }

    public String getMacIdOfUnit() {
        return macIdOfUnit;
    }

    public void setMacIdOfUnit(String macIdOfUnit) {
        this.macIdOfUnit = macIdOfUnit;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
