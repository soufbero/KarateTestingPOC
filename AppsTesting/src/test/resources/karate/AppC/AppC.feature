Feature: AppC Testing

  Background:
    * def AppName = 'AppC'
    * header Content-Type = 'application/xml'
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * def EncodingUtils = Java.type('com.souf.karate.EncodingUtils')
    * def EncryptionUtils = Java.type('com.souf.karate.EncryptionUtils')
    * def DbUtils = Java.type('com.souf.karate.DbUtils')
    * def KafkaUtils = Java.type('com.souf.karate.KafkaUtils')

  Scenario: AppC API 1 valid username and password
    Given url urlAppCapi1
    And request EncodingUtils.decode('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPnNvdWZwYXNzPC9wYXNzd29yZD4KPC9BcGlPbmVSZXF1ZXN0Pg==')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiTwoResponse/responseMessage') == 'Successfully Logged in'
    * def tranId = responseHeaders['test_tran_id'][0]
    * if (validateKafka == true || validateDB == true) sleep(15000)
    * json dbEventValidationResult = DbUtils.validateDBEvents(AppName,tranId,[6])
    * print dbEventValidationResult
    * match dbEventValidationResult.passed == true
    * json dbEventValueValidationResult = DbUtils.validateValueInDBEvent(AppName,tranId,6,'MESSAGE_TXT',null)
    * print dbEventValueValidationResult
    * match dbEventValueValidationResult.passed == true
    * json dbLoginRequestValidationResult = DbUtils.validateDBLogin(AppName,true,tranId,1,'soufiane','Login Successful')
    * print dbLoginRequestValidationResult
    * match dbLoginRequestValidationResult.passed == true
    * json dbEncryptionValidationResult = EncryptionUtils.validateDBEncryptionFromLoginRequest('PASSWORD',EncodingUtils.decode('c291ZnBhc3M='),dbLoginRequestValidationResult)
    * print dbEncryptionValidationResult
    * match dbEncryptionValidationResult.passed == true
    * json kafkaEventValidationResult = KafkaUtils.validateKafkaEvents(kafkaTopic3,tranId,[6])
    * print kafkaEventValidationResult
    * match kafkaEventValidationResult.passed == true
    * json kafkaTopicValidationResult = KafkaUtils.validateKafkaTopics(tranId,[kafkaTopic3,kafkaTopic4])
    * print kafkaTopicValidationResult
    * match kafkaTopicValidationResult.passed == true
    * json kafkaLoginRequestValidationResult = KafkaUtils.validateKafkaLogin(kafkaTopic4,true,tranId,1,'soufiane','Login Successful')
    * print kafkaLoginRequestValidationResult
    * match kafkaLoginRequestValidationResult.passed == true
    * json kafkaEncryptionValidationResult = EncryptionUtils.validateKafkaEncryptionFromLoginRequest('password',EncodingUtils.decode('c291ZnBhc3M='),kafkaLoginRequestValidationResult)
    * print kafkaEncryptionValidationResult
    * match kafkaEncryptionValidationResult.passed == true

  Scenario: AppC API 1 invalid username and valid password
    Given url urlAppCapi1
    And request EncodingUtils.decode('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+PC91c2VyTmFtZT4KICAgIDxwYXNzd29yZD5zb3VmcGFzczwvcGFzc3dvcmQ+CjwvQXBpT25lUmVxdWVzdD4=')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiTwoResponse/responseMessage') == 'Bad Login'
    * def tranId = responseHeaders['test_tran_id'][0]
    * if (validateKafka == true || validateDB == true) sleep(15000)
    * json dbEventValidationResult = DbUtils.validateDBEvents(AppName,tranId,[5])
    * print dbEventValidationResult
    * match dbEventValidationResult.passed == true
    * json dbEventValueValidationResult = DbUtils.validateValueInDBEvent(AppName,tranId,5,'MESSAGE_TXT','Bad Username')
    * print dbEventValueValidationResult
    * match dbEventValueValidationResult.passed == true
    * json dbLoginRequestValidationResult = DbUtils.validateDBLogin(AppName,false,tranId,1,null,'Bad Username')
    * print dbLoginRequestValidationResult
    * match dbLoginRequestValidationResult.passed == true
    * json kafkaEventValidationResult = KafkaUtils.validateKafkaEvents(kafkaTopic3,tranId,[5])
    * print kafkaEventValidationResult
    * match kafkaEventValidationResult.passed == true
    * json kafkaTopicValidationResult = KafkaUtils.validateKafkaTopics(tranId,[kafkaTopic3,kafkaTopic5])
    * print kafkaTopicValidationResult
    * match kafkaTopicValidationResult.passed == true
    * json kafkaLoginRequestValidationResult = KafkaUtils.validateKafkaLogin(kafkaTopic5,false,tranId,1,null,'Bad Username')
    * print kafkaLoginRequestValidationResult
    * match kafkaLoginRequestValidationResult.passed == true

  Scenario: AppC API 1 valid username and invalid password
    Given url urlAppCapi1
    And request EncodingUtils.decode('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPjwvcGFzc3dvcmQ+CjwvQXBpT25lUmVxdWVzdD4=')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiTwoResponse/responseMessage') == 'Bad Login'
    * def tranId = responseHeaders['test_tran_id'][0]
    * if (validateKafka == true || validateDB == true) sleep(15000)
    * json dbEventValidationResult = DbUtils.validateDBEvents(AppName,tranId,[5])
    * print dbEventValidationResult
    * match dbEventValidationResult.passed == true
    * json dbEventValueValidationResult = DbUtils.validateValueInDBEvent(AppName,tranId,5,'MESSAGE_TXT','Bad Password')
    * print dbEventValueValidationResult
    * match dbEventValueValidationResult.passed == true
    * json dbLoginRequestValidationResult = DbUtils.validateDBLogin(AppName,false,tranId,1,'soufiane','Bad Password')
    * print dbLoginRequestValidationResult
    * match dbLoginRequestValidationResult.passed == true
    * json kafkaEventValidationResult = KafkaUtils.validateKafkaEvents(kafkaTopic3,tranId,[5])
    * print kafkaEventValidationResult
    * match kafkaEventValidationResult.passed == true
    * json kafkaTopicValidationResult = KafkaUtils.validateKafkaTopics(tranId,[kafkaTopic3,kafkaTopic5])
    * print kafkaTopicValidationResult
    * match kafkaTopicValidationResult.passed == true
    * json kafkaLoginRequestValidationResult = KafkaUtils.validateKafkaLogin(kafkaTopic5,false,tranId,1,'soufiane','Bad Password')
    * print kafkaLoginRequestValidationResult
    * match kafkaLoginRequestValidationResult.passed == true

  Scenario: AppC API 1 invalid username and password
    Given url urlAppCapi1
    And request EncodingUtils.decode('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+PC91c2VyTmFtZT4KICAgIDxwYXNzd29yZD48L3Bhc3N3b3JkPgo8L0FwaU9uZVJlcXVlc3Q+')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiTwoResponse/responseMessage') == 'Bad Login'
    * def tranId = responseHeaders['test_tran_id'][0]
    * if (validateKafka == true || validateDB == true) sleep(15000)
    * json dbEventValidationResult = DbUtils.validateDBEvents(AppName,tranId,[5])
    * print dbEventValidationResult
    * match dbEventValidationResult.passed == true
    * json dbEventValueValidationResult = DbUtils.validateValueInDBEvent(AppName,tranId,5,'MESSAGE_TXT','Bad Username and Password')
    * print dbEventValueValidationResult
    * match dbEventValueValidationResult.passed == true
    * json dbLoginRequestValidationResult = DbUtils.validateDBLogin(AppName,false,tranId,1,null,'Bad Username and Password')
    * print dbLoginRequestValidationResult
    * match dbLoginRequestValidationResult.passed == true
    * json kafkaEventValidationResult = KafkaUtils.validateKafkaEvents(kafkaTopic3,tranId,[5])
    * print kafkaEventValidationResult
    * match kafkaEventValidationResult.passed == true
    * json kafkaTopicValidationResult = KafkaUtils.validateKafkaTopics(tranId,[kafkaTopic3,kafkaTopic5])
    * print kafkaTopicValidationResult
    * match kafkaTopicValidationResult.passed == true
    * json kafkaLoginRequestValidationResult = KafkaUtils.validateKafkaLogin(kafkaTopic5,false,tranId,1,null,'Bad Username and Password')
    * print kafkaLoginRequestValidationResult
    * match kafkaLoginRequestValidationResult.passed == true
