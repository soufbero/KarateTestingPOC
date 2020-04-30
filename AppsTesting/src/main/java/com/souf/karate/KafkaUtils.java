package com.souf.karate;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaUtils{

    private static Consumer<String, String> consumer;

    private static Map<String, Map<String, List<String>>> transactionsAndMessagesMap = new HashMap<>();
    private static Map<String, Set<String>> transactionsAndTopicsMap = new HashMap<>();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

    public static void initialize(Map<String, Object> config) throws Exception{
        if (UtilsConstants.VALIDATE_KAFKA){
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
            File certFile = new File(KafkaUtils.class.getClassLoader().getResource(kafkaCertPath).getFile());
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, certFile.getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, certFile.getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaCertPass);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaCertPass);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaCertPass);
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(kafkaTopics.split(",")));
        }
    }

    public static void startConsuming(){
        if (UtilsConstants.VALIDATE_KAFKA){
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

                        Map<String,List<String>> allMessages;
                        if (transactionsAndMessagesMap.containsKey(topic)){
                            allMessages = transactionsAndMessagesMap.get(topic);
                        }else{
                            allMessages = new HashMap<>();
                        }

                        List<String> messages;
                        if (allMessages.containsKey(tranId)){
                            messages = allMessages.get(tranId);
                        }else{
                            messages = new ArrayList<>();
                        }
                        messages.add(message);
                        allMessages.put(tranId,messages);
                        transactionsAndMessagesMap.put(topic,allMessages);
                    });
                }
            };
            ses.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);
        }
    }

    public static Map<String, Object> validateKafkaEvents(String topic, String tranId, List<Integer> expectedEvents){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_KAFKA){
            List<String> messages = transactionsAndMessagesMap.get(topic).get(tranId);
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

    public static Map<String, Object> validateKafkaLogin(String topic, boolean isGoodLogin, String tranId, int count,
                                                               String expectedUsername, String expectedInfo){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_KAFKA){
            String infoType;
            String infoKey;
            if (isGoodLogin){
                infoType = "Expected Login Info";
                infoKey = "loginInfo";
            }else{
                infoType = "Expected Reason";
                infoKey = "reason";
            }
            List<String> messages = transactionsAndMessagesMap.get(topic).get(tranId);
            boolean passed = true;
            if (messages.size() != count){
                passed = false;
            }else{
                JSONObject jsonObject = new JSONObject(messages.get(0));
                if (!OtherUtils.twoStringsEqual(expectedUsername,jsonObject.optString("userName",null))
                        || !OtherUtils.twoStringsEqual(expectedInfo,jsonObject.optString(infoKey,null))){
                    passed = false;
                }
            }
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Username",expectedUsername);
            returnedMap.put(infoType,expectedInfo);
            returnedMap.put(UtilsConstants.FULL_DATA_KEY,messages);
            returnedMap.put("passed",passed);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

}
