package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.ContinueTruckingSimulation;

import java.util.logging.Logger;

public class TruckingCheckInSimulationActor extends UntypedActor {
    private static Logger logger = Logger.getLogger(BalanceSimulationActor.class.getName());

    public void onReceive(Object object) {
        if (object instanceof ContinueTruckingSimulation){
            try{
                TruckingSimulationDataDriver.createCheckInStatusUpdates();
                self().tell(new ContinueTruckingSimulation(), self());
            } catch (Exception e){
                logger.severe("Unable to create CheckIn Status Updates due to: "+e.getMessage());
            }
        }
    }
}
