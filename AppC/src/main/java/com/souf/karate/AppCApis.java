package com.souf.karate;

import com.souf.karate.domain.api.ApiRequest;
import com.souf.karate.domain.api.ApiTwoResponse;
import com.souf.karate.service.AuditDataLogger;
import com.souf.karate.util.Constants;
import com.souf.karate.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class AppCApis {

    @Value("${add.test.header}")
    private boolean addTestHeader;

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${application.name}")
    private String applicationName;

    @Value("${kafka.topic.3}")
    private String topic3;
    @Value("${kafka.topic.4}")
    private String topic4;
    @Value("${kafka.topic.5}")
    private String topic5;

    private final AuditDataLogger auditDataLogger;

    @Autowired
    public AppCApis(AuditDataLogger auditDataLogger) {
        this.auditDataLogger = auditDataLogger;
    }

    @PostMapping(value = {"/dev/api1","/qa/api1"},
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ApiTwoResponse> apiMethod1(@RequestBody ApiRequest apiRequest, HttpServletRequest request){
        String env = request.getRequestURI().startsWith("/dev/") ? "DEV" : "QA";

        String tranId = UUID.randomUUID().toString();

        HttpHeaders responseHeaders = new HttpHeaders();
        if (addTestHeader){
            responseHeaders.set(Constants.TEST_HEADER,tranId);
        }

        String reason = null;

        if (Utils.isEmptyOrNull(apiRequest.getUserName()) && Utils.isEmptyOrNull(apiRequest.getPassword())){
            reason = Constants.BAD_USERNAME_AND_PASS;
        }else if (Utils.isEmptyOrNull(apiRequest.getUserName())){
            reason = Constants.BAD_USERNAME;
        }else if (Utils.isEmptyOrNull(apiRequest.getPassword())){
            reason = Constants.BAD_PASS;
        }

        if (reason != null){
            auditDataLogger.logEvent(5,tranId,reason,env,topic3,applicationName);
            String tmpUsername = reason.equals(Constants.BAD_PASS) ? apiRequest.getUserName() : null;
            auditDataLogger.logBadLogin(tranId,reason,tmpUsername,env,topic5,applicationName);
            return ResponseEntity.ok().headers(responseHeaders).body(new ApiTwoResponse("Bad Login"));
        }

        if (encryptionEnabled){
            if (env.equals("DEV")){
                apiRequest.setPassword(Utils.encryptDev(apiRequest.getPassword()));
            }else {
                apiRequest.setPassword(Utils.encryptQa(apiRequest.getPassword()));
            }
        }

        auditDataLogger.logEvent(6,tranId,null,env,topic3,applicationName);
        auditDataLogger.logGoodLogin(tranId, apiRequest, env, topic4, applicationName);

        return ResponseEntity.ok().headers(responseHeaders).body(new ApiTwoResponse("Successfully Logged in"));
    }

}
