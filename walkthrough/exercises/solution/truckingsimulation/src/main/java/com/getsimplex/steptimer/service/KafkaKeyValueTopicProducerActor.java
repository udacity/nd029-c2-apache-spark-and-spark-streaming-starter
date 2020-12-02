package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.KafkaTopicMessage;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Optional;
import java.util.logging.Logger;

public class KafkaKeyValueTopicProducerActor extends UntypedActor {
    private static Logger logger = Logger.getLogger(KafkaKeyValueTopicProducerActor.class.getName());
    private Optional<Producer<String,String>> kafkaProducer = Optional.empty();


    public void onReceive(Object object){

        if (object instanceof KafkaTopicMessage) {
            KafkaTopicMessage kafkaTopicMessage = (KafkaTopicMessage) object;
            if (!kafkaProducer.isPresent()){
                logger.info("Creating Kafka Producer");
                kafkaProducer = Optional.of(KafkaProducerUtil.createProducer());
            }
            logger.info("Sending to Kafka Topic: "+kafkaTopicMessage.getTopic()+" message: "+kafkaTopicMessage.getMessage());

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(kafkaTopicMessage.getTopic(), kafkaTopicMessage.getKey(),kafkaTopicMessage.getMessage());
            kafkaProducer.get().send(record);
            kafkaProducer.get().flush();
        }

    }

}
