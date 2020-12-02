package com.getsimplex.steptimer.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sean on 8/16/2016.
 */
public class Token {
    private String originType;
    private String sourceIp;
    private Boolean trusted;
    private Boolean expires;
    private Date expiration;
    private String user;
    private String uuid;
    public Token(){
        uuid = UUID.randomUUID().toString();
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    public Boolean getExpires() {
        return expires;
    }

    public void setExpires(Boolean expires) {
        this.expires = expires;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
