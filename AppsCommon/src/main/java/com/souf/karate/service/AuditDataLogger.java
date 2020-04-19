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
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditDataLogger {

    private final Logger logger = LoggerFactory.getLogger(AuditDataLogger.class);

    private final KafkaService kafkaService;
    private final DatabaseService databaseService;

    @Autowired
    public AuditDataLogger(KafkaService kafkaService, DatabaseService databaseService){
        this.kafkaService = kafkaService;
        this.databaseService = databaseService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logEvent(int eventID, String tranId, String message, String env, String kafkaTopic, String appName){
        Event event = Event.builder().eventID(eventID).tranId(tranId).message(message).tranTs(Instant.now()).build();
        kafkaService.sendToKafka(kafkaTopic,tranId,generateStringFromObject(event, env,appName), env);
        databaseService.sendToDB(event, env);
    }

    public void logGoodLogin(String tranId, ApiRequest apiRequest, String env, String kafkaTopic, String appName){
        LoginRequest loginRequest = LoginRequest.builder()
                .tranId(tranId)
                .userName(apiRequest.getUserName())
                .password(apiRequest.getPassword())
                .loginInfo("Login Successful")
                .tranTs(Instant.now())
                .build();
        kafkaService.sendToKafka(kafkaTopic,tranId,generateStringFromObject(loginRequest, env,appName), env);
        databaseService.sendToDB(loginRequest, env);
    }

    public void logBadLogin(String tranId, String reason, String userName, String env, String kafkaTopic, String appName){
        BadLoginRequest badLoginRequest = BadLoginRequest.builder()
                .tranId(tranId)
                .reason(reason)
                .userName(userName)
                .tranTs(Instant.now())
                .build();
        kafkaService.sendToKafka(kafkaTopic,tranId,generateStringFromObject(badLoginRequest, env,appName), env);
        databaseService.sendToDB(badLoginRequest, env);
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
