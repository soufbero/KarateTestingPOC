package com.souf.karate;

import com.souf.karate.domain.api.ApiRequest;
import com.souf.karate.domain.api.ApiOneResponse;
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
public class AppAApis {

    @Value("${add.test.header}")
    private boolean addTestHeader;

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${application.name}")
    private String applicationName;

    @Value("${kafka.topic.1}")
    private String topic1;

    private final AuditDataLogger auditDataLogger;

    @Autowired
    public AppAApis(AuditDataLogger auditDataLogger) {
        this.auditDataLogger = auditDataLogger;
    }

    @PostMapping(value = {"/dev/api1","/qa/api1"},
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ApiOneResponse> apiMethod1(@RequestBody ApiRequest apiRequest, HttpServletRequest request){
        String env = request.getRequestURI().startsWith("/dev/") ? "DEV" : "QA";

        String tranId = UUID.randomUUID().toString();

        if (encryptionEnabled && !Utils.isEmptyOrNull(apiRequest.getPassword())){
            if (env.equals("DEV")){
                apiRequest.setPassword(Utils.encryptDev(apiRequest.getPassword()));
            }else {
                apiRequest.setPassword(Utils.encryptQa(apiRequest.getPassword()));
            }
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

}
