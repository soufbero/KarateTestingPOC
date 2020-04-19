package com.souf.karate;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Component
public class KafkaConsumerScheduler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerScheduler.class);

    @Value("${kafka.brokers.dev}")
    private String brokersDEV;
    @Value("${kafka.brokers.qa}")
    private String brokersQA;
    @Value("${kafka.consumer.group.id}")
    private String consumerGroupId;
    @Value("${kafka.consumer.strategy}")
    private String consumerStrategy;
    @Value("${kafka.topic.1}")
    private String topic1;
    @Value("${kafka.topic.2}")
    private String topic2;
    @Value("${kafka.topic.3}")
    private String topic3;
    @Value("${kafka.topic.4}")
    private String topic4;
    @Value("${kafka.topic.5}")
    private String topic5;

    private Consumer<String, String> consumerDEV;
    private Consumer<String, String> consumerQA;

    @PostConstruct
    private void initialize(){
        Properties propsDev = new Properties();
        propsDev.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsDev.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsDev.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersDEV);
        propsDev.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        propsDev.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerStrategy);
        consumerDEV = new KafkaConsumer<>(propsDev);

        Properties propsQA = new Properties();
        propsQA.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsQA.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsQA.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersQA);
        propsQA.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        propsQA.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerStrategy);
        consumerQA = new KafkaConsumer<>(propsQA);

        consumerDEV.subscribe(Arrays.asList(topic1,topic2,topic3,topic4,topic5));
        consumerQA.subscribe(Arrays.asList(topic1,topic2,topic3,topic4,topic5));
    }

    @Scheduled(fixedRate = 1000)
    private void consumerScheduler(){
        ConsumerRecords<String, String> consumerRecordsDev = consumerDEV.poll(Duration.ofMillis(10));
        ConsumerRecords<String, String> consumerRecordsQa = consumerQA.poll(Duration.ofMillis(10));

        if (!consumerRecordsDev.isEmpty()){
            consumerRecordsDev.forEach(record -> logger.info("DEV --- " + record.topic() + " --- " + record.value()));
        }

        if (!consumerRecordsQa.isEmpty()){
            consumerRecordsQa.forEach(record -> logger.info("QA --- " + record.topic() + " --- " + record.value()));
        }
    }
}
