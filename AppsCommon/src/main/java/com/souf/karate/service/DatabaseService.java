package com.souf.karate.service;

import com.souf.karate.domain.db.BadLoginRequest;
import com.souf.karate.domain.db.Event;
import com.souf.karate.domain.db.LoginRequest;
import com.souf.karate.repository.dev.BadLoginRequestRepositoryDEV;
import com.souf.karate.repository.dev.EventRepositoryDEV;
import com.souf.karate.repository.dev.LoginRequestRepositoryDEV;
import com.souf.karate.repository.qa.BadLoginRequestRepositoryQA;
import com.souf.karate.repository.qa.EventRepositoryQA;
import com.souf.karate.repository.qa.LoginRequestRepositoryQA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Value("${db.enabled}")
    private boolean dbEnabled;

    private final EventRepositoryDEV eventRepositoryDEV;
    private final EventRepositoryQA eventRepositoryQA;
    private final LoginRequestRepositoryDEV loginRequestRepositoryDEV;
    private final LoginRequestRepositoryQA loginRequestRepositoryQA;
    private final BadLoginRequestRepositoryDEV badLoginRequestRepositoryDEV;
    private final BadLoginRequestRepositoryQA badLoginRequestRepositoryQA;

    @Autowired
    public DatabaseService(EventRepositoryDEV eventRepositoryDEV,
                           EventRepositoryQA eventRepositoryQA,
                           LoginRequestRepositoryDEV loginRequestRepositoryDEV,
                           LoginRequestRepositoryQA loginRequestRepositoryQA,
                           BadLoginRequestRepositoryDEV badLoginRequestRepositoryDEV,
                           BadLoginRequestRepositoryQA badLoginRequestRepositoryQA) {
        this.eventRepositoryDEV = eventRepositoryDEV;
        this.eventRepositoryQA = eventRepositoryQA;
        this.loginRequestRepositoryDEV = loginRequestRepositoryDEV;
        this.loginRequestRepositoryQA = loginRequestRepositoryQA;
        this.badLoginRequestRepositoryDEV = badLoginRequestRepositoryDEV;
        this.badLoginRequestRepositoryQA = badLoginRequestRepositoryQA;
    }

    public void sendToDB(Event event, String env){
        if (dbEnabled){
            if (env.equals("DEV")){
                eventRepositoryDEV.save(event);
            }else if (env.equals("QA")){
                eventRepositoryQA.save(event);
            }
        }
    }

    public void sendToDB(LoginRequest loginRequest, String env){
        if (dbEnabled){
            if (env.equals("DEV")){
                loginRequestRepositoryDEV.save(loginRequest);
            }else if (env.equals("QA")){
                loginRequestRepositoryQA.save(loginRequest);
            }
        }
    }

    public void sendToDB(BadLoginRequest badLoginRequest, String env){
        if (dbEnabled){
            if (env.equals("DEV")){
                badLoginRequestRepositoryDEV.save(badLoginRequest);
            }else if (env.equals("QA")){
                badLoginRequestRepositoryQA.save(badLoginRequest);
            }
        }
    }
}
