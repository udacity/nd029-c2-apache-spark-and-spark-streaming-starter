package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.LoginToken;
import com.google.gson.Gson;
import com.getsimplex.steptimer.model.RapidStepTest;
import spark.Request;
import com.getsimplex.steptimer.utils.GsonFactory;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.*;


/**
 * Created by sean on 9/7/2016.
 */
public class SaveRapidStepTest {
    private static Gson gson = GsonFactory.getGson();


    public static void save(String rapidStepTestString) throws Exception{

        RapidStepTest rapidStepTest = gson.fromJson(rapidStepTestString, RapidStepTest.class);
        JedisData.loadToJedis(rapidStepTest, RapidStepTest.class);
    }
}
