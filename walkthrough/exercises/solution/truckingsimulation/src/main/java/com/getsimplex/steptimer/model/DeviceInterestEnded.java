package com.getsimplex.steptimer.model;

import org.eclipse.jetty.websocket.api.Session;

/**
 * Created by sean on 9/7/2016.
 */
public class DeviceInterestEnded {

    Session interestedSession;
    String interestedUser;
    String deviceId;

    public Session getInterestedSession() {
        return interestedSession;
    }

    public void setInterestedSession(Session interestedSession) {
        this.interestedSession = interestedSession;
    }

    public String getInterestedUser() {
        return interestedUser;
    }

    public void setInterestedUser(String interestedUser) {
        this.interestedUser = interestedUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
