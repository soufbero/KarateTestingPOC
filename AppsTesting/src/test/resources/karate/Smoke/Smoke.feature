Feature: Smoke Tests

  Background:
    * header Content-Type = 'application/xml'
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * def EncodingUtils = Java.type('com.souf.karate.EncodingUtils')
    * def EncryptionUtils = Java.type('com.souf.karate.EncryptionUtils')
    * def DbUtils = Java.type('com.souf.karate.DbUtils')
    * def KafkaUtils = Java.type('com.souf.karate.KafkaUtils')
    * def OtherUtils = Java.type('com.souf.karate.OtherUtils')

  @AppA
  Scenario: AppA Smoke
    Given url urlAppAapi1
    And request EncodingUtils.decode('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPnNvdWZwYXNzPC9wYXNzd29yZD4KPC9BcGlPbmVSZXF1ZXN0Pg==')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiOneResponse/status') == 'Y'
    * def tranId = responseHeaders['test_tran_id'][0]
    * if (validateKafka == true || validateDB == true) sleep(15000)
    * json dbEventValidationResult = DbUtils.validateDBEvents(tranId,[1,2])
    * print dbEventValidationResult
    * match dbEventValidationResult.passed == true
    * json dbEncryptionValidationResult = EncryptionUtils.validateDBEncryptionFromEvents(1,'MESSAGE_TXT','/ApiRequest/password',EncodingUtils.decode('c291ZnBhc3M='),dbEventValidationResult)
    * print dbEncryptionValidationResult
    * match dbEncryptionValidationResult.passed == true
    * json kafkaEventValidationResult = KafkaUtils.validateKafkaEvents(tranId,[1,2])
    * print kafkaEventValidationResult
    * match kafkaEventValidationResult.passed == true
    * json kafkaTopicValidationResult = KafkaUtils.validateKafkaTopics(tranId,[kafkaTopic1])
    * print kafkaTopicValidationResult
    * match kafkaTopicValidationResult.passed == true
    * json kafkaEncryptionValidationResult = EncryptionUtils.validateKafkaEncryptionFromEvents(1,'message','/ApiRequest/password',EncodingUtils.decode('c291ZnBhc3M='),kafkaEventValidationResult)
    * print kafkaEncryptionValidationResult
    * match kafkaEncryptionValidationResult.passed == true

  @AppB
  Scenario: AppB Smoke
    * print 'Test AppB'

  @AppC
  Scenario: AppC Smoke
    * print 'Test AppC'

