package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.DeviceInterest;
import com.getsimplex.steptimer.model.DeviceInterestEnded;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

@WebSocket(maxIdleTime = Integer.MAX_VALUE)
public class TimerUIWebSocket {


    @OnWebSocketConnect
    public void onConnect(Session session){
        DeviceInterest deviceInterest = new DeviceInterest();
        deviceInterest.setDeviceId(session.getRemoteAddress().getHostString());//this message will be unique for this user, and will keep their Timer sessions limited to 1
        deviceInterest.setInterestedSession(session);
        deviceInterest.setInterestedUser("clinicmanager");
        MessageIntake.route(deviceInterest);//notify Device Actor of a listening device
    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String message){
        DeviceInterestEnded deviceInterestEnded = new DeviceInterestEnded();
        deviceInterestEnded.setDeviceId(session.getRemoteAddress().getHostString());
        deviceInterestEnded.setInterestedSession(session);
        deviceInterestEnded.setInterestedUser("clinicmanager");
        MessageIntake.route(deviceInterestEnded);// notify Device Actor of a closed session
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message){

    }
}
