package com.getsimplex.steptimer.service;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import com.getsimplex.steptimer.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.websocket.api.Session;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by sean on 8/16/2016.
 */
public class MessageRouteByType extends UntypedActor {

    private static Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
    private static Gson gson = new Gson();
    private static Logger logger = Logger.getLogger(MessageRouteByType.class.getName());
    private ActorRef deviceRouter = getContext().actorOf(new RoundRobinPool(1).props(Props.create(DeviceRouter.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef browserRouter = getContext().actorOf(new RoundRobinPool(1).props(Props.create(BrowserRouter.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef simulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(SimulationActor.class)));
    private ActorRef kafkaRiskTopicActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(KafkaRiskTopicConsumerActor.class)));
    private ActorRef kafkaRiskTopicProducerActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(KafkaRiskTopicProducerActor.class)));
    private ActorRef balanceSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(BalanceSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef atmVisitsSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(ATMVisitsSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef depositSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(DepositSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef withdrawalSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(ATMWithdrawalSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef truckingCheckinSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(TruckingCheckInSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef truckingFuelLevelSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(TruckingFuelLevelSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef truckingGearPositionSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(TruckingGearPositionSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef truckingPaymentSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(TruckingPaymentSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts
    private ActorRef truckingVehicleStatusSimulationActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(TruckingVehicleStatusSimulationActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts

    private ActorRef kafkaTopicProducerActor = getContext().actorOf(new RoundRobinPool(1).props(Props.create(KafkaKeyValueTopicProducerActor.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts


    public void onReceive(Object object){

        if (object instanceof KafkaTopicMessage){
            kafkaTopicProducerActor.tell(object, self());
        }
        else if (object instanceof ContinueBalanceSimulation){
            balanceSimulationActor.tell(object, self());
            atmVisitsSimulationActor.tell(object, self());
            withdrawalSimulationActor.tell(object, self());
            depositSimulationActor.tell(object, self());
        } else if(object instanceof ContinueTruckingSimulation){
            truckingCheckinSimulationActor.tell(object, self());
            truckingFuelLevelSimulationActor.tell(object, self());
            truckingGearPositionSimulationActor.tell(object, self());
            truckingPaymentSimulationActor.tell(object, self());
            truckingVehicleStatusSimulationActor.tell(object, self());
        }

        else if (object instanceof DeviceInterest){
            deviceRouter.tell(object, self());
        }
        else if (object instanceof StediEvent){
            kafkaRiskTopicProducerActor.tell(object, self());
        }
        else if (object instanceof StartReceivingKafkaMessages){
            kafkaRiskTopicActor.tell(object, self());
        }
        else if (object instanceof SessionMessageResponse){
            SessionMessageResponse sessionMessage = (SessionMessageResponse) object;

            Session session = sessionMessage.session;
            String message= sessionMessage.message;
            ValidationResponse validationResponse = sessionMessage.validationResponse;
//            Map<String, String> jsonProperties = gson.fromJson(message, stringStringMap);
//            String token = jsonProperties.get("userKey");

            if (MessageSourceTypes.DEVICE.equals(validationResponse.getOriginType())){
                DeviceMessage deviceMessage = new DeviceMessage();
//                deviceMessage.setSessionKey(token);
                deviceMessage.setMessageOrigin(validationResponse.getOriginType());
                deviceMessage.setMessage(message);
                deviceMessage.setSession(session);
                deviceRouter.tell(deviceMessage, self());

            } else if(MessageSourceTypes.BROWSER.equals(validationResponse.getOriginType())){
                BrowserMessage browserMessage = new BrowserMessage();
//                browserMessage.setSessionKey(token);
                browserMessage.setMessageOrigin(validationResponse.getOriginType());
                browserMessage.setSession(session);
                browserMessage.setMessage(message);
                browserRouter.tell(browserMessage, self());

                if (message.contains("StartReading~")){
                    String[] messageParts = message.split("~");
                    if (messageParts.length>1){
                        String userToken = messageParts[1];
                        try {
                            User user = TokenService.getUserFromToken(userToken).get();

                            DeviceInterest deviceInterest = new DeviceInterest();
                            deviceInterest.setDeviceId(user.getDeviceNickName());
                            deviceInterest.setInterestedSession(session);
                            deviceRouter.tell(deviceInterest, self());
                        } catch (Exception e){
                            logger.severe(e.getMessage());
                        }
                    }
                }

            } else if(MessageSourceTypes.SERVICE.equals(validationResponse.getOriginType())){

                if (message.contains("StopReading~")) {
                        DeviceInterestEnded deviceInterestEnded = new DeviceInterestEnded();
//                        deviceInterestEnded.setDeviceId(deviceId);
                        deviceInterestEnded.setInterestedSession(session);
                        deviceRouter.tell(deviceInterestEnded, self());
                }
            }else{
                logger.info("Unable to route Message Source Type: "+validationResponse.getOriginType()+" from origin IP address: "+validationResponse.getOriginIpAddress());//+" with token: "+token);
            }

        } else if (object instanceof DeviceMessage){
            deviceRouter.tell(object, self());
        } else if (object instanceof StartSimulation || object instanceof StopSimulation || object instanceof ContinueSimulation){
            simulationActor.tell(object, self());
        }
    }
}
