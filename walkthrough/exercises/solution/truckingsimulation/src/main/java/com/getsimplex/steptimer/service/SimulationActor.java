package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.BrowserMessage;
import com.getsimplex.steptimer.model.ContinueSimulation;
import com.getsimplex.steptimer.model.StartSimulation;
import com.getsimplex.steptimer.model.StopSimulation;

import java.util.logging.Logger;

/**
 * Created by sean on 8/16/2016.
 */
public class SimulationActor extends UntypedActor {
    private static Logger logger = Logger.getLogger(SimulationActor.class.getName());
    private static boolean stop = false;

    public void onReceive(Object object){

        if (object instanceof StartSimulation){
            stop = false;// in case this is a stop/start situation
            StartSimulation startSimulation = (StartSimulation) object;
            logger.info("SimulationActor received StartSimulationMessage to start simulation for: "+startSimulation.getNumberOfCustomers()+" test customers");
            try{
                SimulationDataDriver.setSimulationActive(true);
                logger.info("Creating test customers...");
                SimulationDataDriver.generateTestCustomers(startSimulation.getNumberOfCustomers());//the Kafka Redis Topic needs to be active at this point since this only happens once and they will miss it
                logger.info("Starting infinite loop for test data");
                SimulationDataDriver.createRapidStepTests();
                self().tell(new ContinueSimulation(), self());//continue simulation
            } catch (Exception e){
                logger.severe(e.getMessage());
            }
        } else if (object instanceof StopSimulation){
            stop = true;//any subsequent messages to continue will be ignored until we are told to start again
            SimulationDataDriver.setSimulationActive(false);
            logger.info("Received request to stop simulation, stopping until further notice...");
        }
        else if (object instanceof ContinueSimulation && !stop){
            try {
                logger.info("Adding more test data...");
                SimulationDataDriver.setSimulationActive(true);
                SimulationDataDriver.createRapidStepTests();
                self().tell(new ContinueSimulation(), self());//continue simulation
            } catch (Exception e){
                logger.severe(e.getMessage());
            }
        }

    }
}
