package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.ContinueTruckingSimulation;

import java.util.logging.Logger;

import akka.actor.UntypedActor;

public class TruckingVehicleStatusSimulationActor extends UntypedActor {
    private static Logger logger = Logger.getLogger(BalanceSimulationActor.class.getName());

    public void onReceive(Object object) {
        if (object instanceof ContinueTruckingSimulation){
            try{
                TruckingSimulationDataDriver.createVehicleStatusUpdates();
                self().tell(new ContinueTruckingSimulation(), self());
            } catch (Exception e){
                logger.severe("Unable to create Vehicle Status Updates due to: "+e.getMessage());
            }
        }
    }
}
