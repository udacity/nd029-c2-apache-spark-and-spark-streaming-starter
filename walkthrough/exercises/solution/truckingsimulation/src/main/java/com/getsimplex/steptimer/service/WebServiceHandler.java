package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.DeviceMessage;
import com.getsimplex.steptimer.model.Token;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.io.*;
import java.util.Date;

/**
 * Created by sean on 9/15/2016.
 */
public class WebServiceHandler {
    private static Gson gson = new Gson();

    public static String routePdfRequest(Request request, Response response) throws Exception{
        File pdfFile = new File("C:\\temp\\temp.pdf");
        FileInputStream inputStream = new FileInputStream(pdfFile);
        Reader inputStreamReader = new InputStreamReader(inputStream);
        response.header("Content-Type","application/pdf");
        int data = inputStreamReader.read();
        while (data!=-1) {
            byte thisbyte = (byte) data;
            response.raw().getOutputStream().write(thisbyte);
            data = inputStreamReader.read();
        }

        return "Done";
    }

    public static String routeDeviceRequest(Request request) throws Exception{
        DeviceMessage deviceMessage = gson.fromJson(request.body(), DeviceMessage.class);
        try {
            MessageIntake.route(deviceMessage);
        } catch (Exception e) {
            throw e;
        }

        return "Accepted";
    }

}
