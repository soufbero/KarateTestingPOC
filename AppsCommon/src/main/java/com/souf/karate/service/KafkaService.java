package com.souf.karate.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
@ConditionalOnProperty(
        value="kafka.enabled",
        havingValue = "true")
public class KafkaService {

    @Value("${kafka.enabled}")
    private boolean kafkaEnabled;
    @Value("${kafka.brokers.dev}")
    private String kafkaBrokersDEV;
    @Value("${kafka.brokers.qa}")
    private String kafkaBrokersQA;

    private KafkaProducer<String,String> kafkaProducerClientDEV = null;
    private KafkaProducer<String,String> kafkaProducerClientQA = null;

    @PostConstruct
    private void initialize(){
        if (kafkaEnabled){
            Properties props = new Properties();
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersDEV);
            kafkaProducerClientDEV = new KafkaProducer<>(props);

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersQA);
            kafkaProducerClientQA = new KafkaProducer<>(props);
        }
    }

    public void sendToKafka(String topic, String tranId, String message, String env){
        if (kafkaEnabled){
            if (env.equals("DEV")){
                kafkaProducerClientDEV.send(new ProducerRecord<>(topic,tranId,message));
            }else if (env.equals("QA")){
                kafkaProducerClientQA.send(new ProducerRecord<>(topic,tranId,message));
            }
        }
    }
}
