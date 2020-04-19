package com.souf.karate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.karate.domain.api.ApiRequest;
import com.souf.karate.domain.db.BadLoginRequest;
import com.souf.karate.domain.db.Event;
import com.souf.karate.domain.db.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditDataLogger {

    private final Logger logger = LoggerFactory.getLogger(AuditDataLogger.class);

    @Value("${db.enabled}")
    private boolean dbEnabled;

    @Value("${kafka.enabled}")
    private boolean kafkaEnabled;

    @Autowired(required = false)
    private DatabaseService databaseService;
    @Autowired(required = false)
    private KafkaService kafkaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logEvent(int eventID, String tranId, String message, String env, String kafkaTopic, String appName){
        Event event = Event.builder().eventID(eventID).tranId(tranId).message(message).tranTs(Instant.now()).build();
        String objectString = generateStringFromObject(event, env,appName);
        if (dbEnabled){
            databaseService.sendToDB(event, env);
        }
        if (kafkaEnabled){
            kafkaService.sendToKafka(kafkaTopic,tranId,objectString, env);
        }
    }

    public void logGoodLogin(String tranId, ApiRequest apiRequest, String env, String kafkaTopic, String appName){
        LoginRequest loginRequest = LoginRequest.builder()
                .tranId(tranId)
                .userName(apiRequest.getUserName())
                .password(apiRequest.getPassword())
                .loginInfo("Login Successful")
                .tranTs(Instant.now())
                .build();
        String objectString = generateStringFromObject(loginRequest, env,appName);
        if (dbEnabled){
            databaseService.sendToDB(loginRequest, env);
        }
        if (kafkaEnabled){
            kafkaService.sendToKafka(kafkaTopic,tranId,objectString, env);
        }
    }

    public void logBadLogin(String tranId, String reason, String userName, String env, String kafkaTopic, String appName){
        BadLoginRequest badLoginRequest = BadLoginRequest.builder()
                .tranId(tranId)
                .reason(reason)
                .userName(userName)
                .tranTs(Instant.now())
                .build();
        String objectString = generateStringFromObject(badLoginRequest, env,appName);
        if (dbEnabled){
            databaseService.sendToDB(badLoginRequest, env);
        }
        if (kafkaEnabled){
            kafkaService.sendToKafka(kafkaTopic,tranId,objectString, env);
        }
    }

    private String generateStringFromObject(Object object, String env, String appName){
        String objectString;
        try{
            objectString =  objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException ex) {
            objectString = "N/A Object could not be parsed";
        }
        logger.info(String.format("%s --- ENV: %s --- %s",appName, env, objectString));
        return objectString;
    }

}
