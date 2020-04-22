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
import java.util.concurrent.atomic.AtomicReference;

public class KafkaUtils {

    private static Consumer<String, String> consumer;

    private static Map<String, List<String>> transactionsAndMessagesMap = new HashMap<>();
    private static Map<String, Set<String>> transactionsAndTopicsMap = new HashMap<>();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

    public static void initializeConsumer(Map<String, Object> config) {
        String kafkaBrokers = (String) config.get("kafkaBrokers");
        String kafkaTopics = (String) config.get("kafkaTopics");
        String kafkaCertPath = (String) config.get("kafkaCertPath");
        String kafkaCertPass = (String) config.get("kafkaCertPass");
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

    public static void startConsuming(){
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

    public static List<String> getTopicsInKafka(String tranId){
        return new ArrayList<>(transactionsAndTopicsMap.get(tranId));
    }

    public static List<String> getMessagesInKafka(String tranId){
        return transactionsAndMessagesMap.get(tranId);
    }

    public static List<Integer> readEvents(String tranId){
        List<Integer> events = new ArrayList<>();
        List<String> messages =  transactionsAndMessagesMap.get(tranId);
        messages.forEach(m ->{
            JSONObject jsonObject = new JSONObject(m);
            if (jsonObject.has("eventID")){
                events.add(jsonObject.getInt("eventID"));
            }
        });
        Collections.sort(events);
        return events;
    }

    public static String getMessageTextForEvent(String tranId, int eventID){
        AtomicReference<String> message = new AtomicReference<>(null);
        List<String> messages =  transactionsAndMessagesMap.get(tranId);
        messages.forEach(m ->{
            JSONObject jsonObject = new JSONObject(m);
            if (jsonObject.has("eventID") && jsonObject.has("message")
                    && jsonObject.getInt("eventID") == eventID){
                message.set(jsonObject.getString("message"));
            }
        });
        return message.get();
    }


}
