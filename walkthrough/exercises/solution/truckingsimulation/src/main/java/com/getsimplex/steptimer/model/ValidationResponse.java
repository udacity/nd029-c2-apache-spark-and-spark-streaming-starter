package com.getsimplex.steptimer.model;

/**
 * Created by sean on 8/16/2016.
 */
public class ValidationResponse {

    private Boolean trusted;
    private String originType;
    private String originIpAddress;
    private Boolean expired;
    private String user;

    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getOriginIpAddress() {
        return originIpAddress;
    }

    public void setOriginIpAddress(String originIpAddress) {
        this.originIpAddress = originIpAddress;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
