package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.ContinueBalanceSimulation;
import com.getsimplex.steptimer.service.BankingSimulationDataDriver;


import java.util.logging.Logger;

/**
 * Created by sean on 8/16/2016.
 */
public class DepositSimulationActor extends UntypedActor {
    private static Logger logger = Logger.getLogger(com.getsimplex.steptimer.service.BalanceSimulationActor.class.getName());
    private static boolean stop = false;

    public void onReceive(Object object){

        if (object instanceof ContinueBalanceSimulation){
            try {
                BankingSimulationDataDriver.createDeposits();
                self().tell(new ContinueBalanceSimulation(), self());//continue simulation
            } catch (Exception e){
                logger.severe(e.getMessage());
            }
        }

    }
}
