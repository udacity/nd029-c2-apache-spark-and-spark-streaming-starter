package com.getsimplex.steptimer.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * Created by Administrator on 1/12/2016.
 */
public class Configuration {

    private static Boolean configFileOnClassPath = false;
    public static Config getConfiguration(){
        Config config = ConfigFactory.load();//this will load from the classpath

        //This overrides classpath config file
        String configFilePath=System.getProperty("config");//ex: /home/workspae/stedi-application/application.conf
        if (configFilePath!=null && configFilePath!=""){
            File configFile=new File(configFilePath);
            config = ConfigFactory.parseFile(configFile);
        }
        return config;
    }

    public static Boolean isConfigFileOnClassPath(){
        return configFileOnClassPath;
    }
}

