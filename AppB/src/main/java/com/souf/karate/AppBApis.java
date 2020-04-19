package com.souf.karate;

import com.souf.karate.domain.api.ApiRequest;
import com.souf.karate.domain.api.ApiOneResponse;
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
public class AppBApis {

    @Value("${add.test.header}")
    private boolean addTestHeader;

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${application.name}")
    private String applicationName;

    @Value("${kafka.topic.1}")
    private String topic1;
    @Value("${kafka.topic.2}")
    private String topic2;

    private final AuditDataLogger auditDataLogger;

    @Autowired
    public AppBApis(AuditDataLogger auditDataLogger) {
        this.auditDataLogger = auditDataLogger;
    }

    @PostMapping(value = {"/dev/api1","/qa/api1"},
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ApiOneResponse> apiMethod1(@RequestBody ApiRequest apiRequest, HttpServletRequest request){
        String env = request.getRequestURI().startsWith("/dev/") ? "DEV" : "QA";

        String tranId = UUID.randomUUID().toString();

        if (encryptionEnabled && !Utils.isEmptyOrNull(apiRequest.getPassword())){
            apiRequest.setPassword(Utils.encrypt(apiRequest.getPassword()));
        }

        auditDataLogger.logEvent(1,tranId, apiRequest.toString(),env,topic1,applicationName);

        ApiOneResponse apiOneResponse = new ApiOneResponse();
        if (Utils.isEmptyOrNull(apiRequest.getUserName())){
            apiOneResponse.setUserName("Not a valid username");
            apiOneResponse.setStatus("N");
        }else if (Utils.isEmptyOrNull(apiRequest.getPassword())){
            apiOneResponse.setUserName(apiRequest.getUserName());
            apiOneResponse.setStatus("N");
        }else{
            apiOneResponse.setUserName(apiRequest.getUserName());
            apiOneResponse.setStatus("Y");
        }

        auditDataLogger.logEvent(2,tranId, apiOneResponse.toString(),env,topic1,applicationName);

        HttpHeaders responseHeaders = new HttpHeaders();
        if (addTestHeader){
            responseHeaders.set(Constants.TEST_HEADER,tranId);
        }
        return ResponseEntity.ok().headers(responseHeaders).body(apiOneResponse);
    }

    @PostMapping(value = {"/dev/api2","/qa/api2"},
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ApiTwoResponse> apiMethod2(@RequestBody ApiRequest apiRequest, HttpServletRequest request){
        String env = request.getRequestURI().startsWith("/dev/") ? "DEV" : "QA";

        String tranId = UUID.randomUUID().toString();

        HttpHeaders responseHeaders = new HttpHeaders();
        if (addTestHeader){
            responseHeaders.set(Constants.TEST_HEADER,tranId);
        }

        if (Utils.isEmptyOrNull(apiRequest.getUserName()) || Utils.isEmptyOrNull(apiRequest.getPassword())){
            auditDataLogger.logEvent(3,tranId,null,env,topic1,applicationName);
            return ResponseEntity.ok().headers(responseHeaders)
                    .body(new ApiTwoResponse("Invalid Username or password"));
        }

        if (encryptionEnabled){
            apiRequest.setPassword(Utils.encrypt(apiRequest.getPassword()));
        }

        auditDataLogger.logEvent(4,tranId,null,env,topic1,applicationName);
        auditDataLogger.logGoodLogin(tranId, apiRequest, env, topic2, applicationName);

        return ResponseEntity.ok().headers(responseHeaders).body(new ApiTwoResponse("Successfully Logged in"));
    }

}
