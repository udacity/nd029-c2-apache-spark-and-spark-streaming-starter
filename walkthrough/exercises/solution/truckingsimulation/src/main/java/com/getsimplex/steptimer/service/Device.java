package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.com.getsimplex.steptimer.sensormessages.SensorMessage;
import com.getsimplex.steptimer.model.DeviceInterest;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;


import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by sean on 9/7/2016.
 */
public class Device extends UntypedActor {
    private HashMap<String, Session> deviceInterestMap = new HashMap<String,Session>();
    private static Gson gson = new Gson();
    Logger logger = Logger.getLogger(Device.class.getName());
    public void onReceive(Object message){
        if (message instanceof DeviceInterest){
            DeviceInterest deviceInterest = (DeviceInterest) message;
            deviceInterestMap.put(deviceInterest.getDeviceId(), deviceInterest.getInterestedSession());
        }

        else if (message instanceof SensorMessage){
            SensorMessage sensorMessage = (SensorMessage) message;
            if(deviceInterestMap.containsKey(sensorMessage.getDeviceId())){
                try {
                    deviceInterestMap.get(sensorMessage.getDeviceId()).getRemote().sendString(gson.toJson(sensorMessage));
                } catch (Exception e){
                    logger.severe("Unable to send message for device Id: "+sensorMessage.getDeviceId());
                }
            }
        }
    }
}
