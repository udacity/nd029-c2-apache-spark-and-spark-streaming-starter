package com.getsimplex.steptimer.model;

import org.eclipse.jetty.websocket.api.Session;

/**
 * Created by sean on 8/11/2016.
 */
public class DeviceMessage {

    private String messageOrigin;
    private String sessionKey;
    private String message;
    private Session session;
    private String deviceId;
    private long date;
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }



    public String getMessageOrigin() {
        return messageOrigin;
    }

    public void setMessageOrigin(String messageOrigin) {
        this.messageOrigin = messageOrigin;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
