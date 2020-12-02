package com.getsimplex.steptimer.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

/**
 * Created by sean on 8/16/2016.
 */
public class MessageIntake {

    private static ActorSystem system = ActorSystem.create("websocket");
    private static ActorRef messageRouter = system.actorOf(new RoundRobinPool(1).props(Props.create(MessageRouteByType.class)));//a pool of one means that only one actor is running at any moment and if it crashes, the actor restarts

    public static synchronized String route(Object message){
        messageRouter.tell(message,ActorRef.noSender());
        return "Sent";
    }
}
