package com.getsimplex.steptimer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Administrator on 12/16/2016.
 */
public class GsonFactory {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    public static Gson getGson() {
        return gson;
    }
}
