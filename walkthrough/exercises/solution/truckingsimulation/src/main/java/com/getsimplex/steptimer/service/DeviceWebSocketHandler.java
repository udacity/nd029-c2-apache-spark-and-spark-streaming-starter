package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sean on 8/10/2016.
 */
@WebSocket()
public class DeviceWebSocketHandler {

    private static Gson gson = new Gson();
    private static Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
    public static String TOKEN_KEY= "userToken";
    private static Logger logger = Logger.getLogger(DeviceWebSocketHandler.class.getName());

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception{
        DeviceInterest deviceInterest = new DeviceInterest();
        deviceInterest.setDeviceId("1234");//this is just a default device id used for testing
        deviceInterest.setInterestedSession(session);
        deviceInterest.setInterestedUser("clinicmanager");
        MessageIntake.route(deviceInterest);//this should make it so new messages from Kafka for device 1234 go to this user's websocket

    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String message){
//        String shortMessage = "StopReading~";
//        SessionMessageResponse sessionMessage = new SessionMessageResponse();
//        sessionMessage.message=shortMessage;
//        sessionMessage.session=session;
//        ValidationResponse validationResponse = new ValidationResponse();
//        validationResponse.setOriginType(MessageSourceTypes.SERVICE);
//        sessionMessage.validationResponse=validationResponse;
//        MessageIntake.route(sessionMessage);

        DeviceInterestEnded deviceInterestEnded = new DeviceInterestEnded();
        deviceInterestEnded.setDeviceId("1234");
        deviceInterestEnded.setInterestedSession(session);
        deviceInterestEnded.setInterestedUser("clinicmanager");
        MessageIntake.route(deviceInterestEnded);// this should prevent trying to send updates to a closed socket
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception{
        if ("StartDemo".equals(message)){ //Demo mode
            session.getRemote().sendString("startTimer");
            for (int i=1;i<=30;i++) {

                Thread.sleep(1000);//sleep for a second

                session.getRemote().sendString("stepCount:"+i);
                System.out.println("Step count: "+i);
            }
        } else if (message.contains("StartReading~")){
            SessionMessageResponse sessionMessage = new SessionMessageResponse();
            sessionMessage.message=message;
            sessionMessage.session=session;
            ValidationResponse validationResponse = new ValidationResponse();
            validationResponse.setOriginType(MessageSourceTypes.BROWSER);
            sessionMessage.validationResponse=validationResponse;
            MessageIntake.route(sessionMessage);
        }
        else {
            Gson gson = new Gson();

            Map<String, String> jsonProps = gson.fromJson(message, stringStringMap);
            if (jsonProps.containsKey(TOKEN_KEY)) {
                String token = jsonProps.get(TOKEN_KEY);
                try {//if they have an invalid (not trusted) token or they have an expired token, close the session
                    //ValidationResponse validationResponse = SessionValidator.validateSession(token, session);
                    ValidationResponse validationResponse = new ValidationResponse();
                    validationResponse.setExpired(false);
                    validationResponse.setTrusted(true);
                    if (validationResponse.getTrusted() && !validationResponse.getExpired()) {
                        if (!SessionValidator.sessionTokens.containsKey(session)) {//java cache from Redis
                            SessionValidator.sessionTokens.put(session, token);
                        }

                        SessionMessageResponse sessionMessage = new SessionMessageResponse();
                        sessionMessage.message = message;
                        sessionMessage.session = session;

                        if (jsonProps.get("interestedUser")!=null && !jsonProps.get("interestedUser").isEmpty()){
                            sessionMessage.messageType=MessageSourceTypes.DEVICE;
                        }

                        MessageIntake.route(sessionMessage);

                    } else if (!validationResponse.getTrusted() || validationResponse.getExpired()) {
                        if (SessionValidator.sessionTokens.containsKey(session)) {
                            SessionValidator.sessionTokens.remove(session);
                            session.close();
                        }
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    session.close();
                }
            } else {
                session.close();
            }
        }

    }

}
