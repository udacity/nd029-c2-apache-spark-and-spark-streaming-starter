package com.getsimplex.steptimer.model;

import org.eclipse.jetty.websocket.api.Session;

/**
 * Created by sean on 8/16/2016.
 */
public interface Message {

    public String getMessageType();

    public void setMessageOrigin(String messageOrigin);

    public String getMessageOrigin();

    public void setSessionKey(String sessionKey);

    public String getSessionKey();

    public void setMessage(String jsonMessage);

    public String getMessage();

    public Session getSession();

    public void setSession(Session session);



}
