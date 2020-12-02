package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.BrowserMessage;

import java.util.logging.Logger;

/**
 * Created by sean on 8/16/2016.
 */
public class BrowserRouter extends UntypedActor {
    private static Logger logger = Logger.getLogger(BrowserRouter.class.getName());

    public void onReceive(Object object){

        if (object instanceof BrowserMessage){
            BrowserMessage message = (BrowserMessage) object;
            logger.info("BrowserRouter received payload: "+message.getMessage());
        }

    }
}
