package com.getsimplex.steptimer.com.getsimplex.steptimer.sensormessages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by sean on 9/7/2016.
 */
public class SensorDeserializer {
    private static Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();

    private static Gson gson = new Gson();

    public static Object deserializeSensorMessage(String sensorMessageString) throws Exception{
        Map<String, String> jsonProps = gson.fromJson(sensorMessageString, stringStringMap);
        if(jsonProps.containsKey("messageType")){
            return deserializeSensorMessage(sensorMessageString, jsonProps.get("messageType"));
        } else throw new Exception("messageType not given in message:"+sensorMessageString);
    }

    private static Object deserializeSensorMessage(String sensorMessage, String messageType) throws ClassNotFoundException{
        Object object =gson.fromJson(sensorMessage, Class.forName(messageType));
        return object;
    }

}
