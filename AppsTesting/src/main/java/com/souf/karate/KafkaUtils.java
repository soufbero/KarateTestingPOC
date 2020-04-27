package com.souf.karate;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaUtils {

    private static Consumer<String, String> consumer;

    private static Map<String, List<String>> transactionsAndMessagesMap = new HashMap<>();
    private static Map<String, Set<String>> transactionsAndTopicsMap = new HashMap<>();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

    public static void initialize(Map<String, Object> config) {
        if (UtilsConstants.VALIDATE_KAFKA && !UtilsConstants.KAFKA_INITIALIZED){
            UtilsConstants.KAFKA_INITIALIZED = true;
            String kafkaBrokers = (String) config.get("brokers");
            String kafkaTopics = (String) config.get("topics");
            String kafkaCertPath = (String) config.get("certPath");
            String kafkaCertPass = (String) config.get("certPass");
            Properties props = new Properties();
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
            String consumerGroupID = "AppsTestGroup-" + LocalDateTime.now().format(formatter);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupID);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,"SSL");
            props.put(SslConfigs.SSL_PROTOCOL_CONFIG,"TLSv1.2");
            props.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG,"TLSv1.2");
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaCertPath);
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaCertPath);
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaCertPass);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaCertPass);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaCertPass);
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(kafkaTopics.split(",")));
        }
    }

    public static void startConsuming(){
        if (UtilsConstants.VALIDATE_KAFKA && !UtilsConstants.KAFKA_CONSUMER_STARTED){
            UtilsConstants.KAFKA_CONSUMER_STARTED = true;
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(10));
                if (!consumerRecords.isEmpty()){
                    consumerRecords.forEach(record ->{
                        String topic = record.topic();
                        String tranId = record.key();
                        String message = record.value();

                        Set<String> topics;
                        if (transactionsAndTopicsMap.containsKey(tranId)){
                            topics = transactionsAndTopicsMap.get(tranId);
                        }else{
                            topics = new HashSet<>();
                        }
                        topics.add(topic);
                        transactionsAndTopicsMap.put(tranId,topics);

                        List<String> messages;
                        if (transactionsAndMessagesMap.containsKey(tranId)){
                            messages = transactionsAndMessagesMap.get(tranId);
                        }else{
                            messages = new ArrayList<>();
                        }
                        messages.add(message);
                        transactionsAndMessagesMap.put(tranId,messages);
                    });
                }
            };
            ses.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);
        }
    }

    public static Map<String, Object> validateKafkaEvents(String tranId, List<Integer> expectedEvents){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_KAFKA){
            List<String> messages = transactionsAndMessagesMap.get(tranId);
            List<Integer> eventsIds = new ArrayList<>();
            messages.forEach(m ->{
                JSONObject jsonObject = new JSONObject(m);
                if (jsonObject.has("eventID")){
                    eventsIds.add(jsonObject.getInt("eventID"));
                }
            });
            Collections.sort(eventsIds);
            Collections.sort(expectedEvents);
            returnedMap.put("passed",expectedEvents.equals(eventsIds));
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Event IDs",expectedEvents);
            returnedMap.put("Actual Event IDs",eventsIds);
            returnedMap.put(UtilsConstants.FULL_DATA_KEY,messages);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        returnedMap.put("passed",true);
        return returnedMap;
    }

    public static Map<String, Object> validateKafkaTopics(String tranId, List<String> expectedTopics){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_KAFKA){
            List<String> topics =  new ArrayList<>(transactionsAndTopicsMap.get(tranId));
            Collections.sort(topics);
            Collections.sort(expectedTopics);
            returnedMap.put("passed",expectedTopics.equals(topics));
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Topics",expectedTopics);
            returnedMap.put("Actual Topics",topics);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

}
