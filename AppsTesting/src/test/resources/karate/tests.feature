Feature: Testing a REST API with Karate

  Background:
    * header Content-Type = 'application/xml'
    * def decodeRequest =
      """
      function(input) {
          var Base64 = Java.type('java.util.Base64');
          var decoded = Base64.getDecoder().decode(input);
          var String = Java.type('java.lang.String');
          return new String(decoded);
      }
      """
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * def EncryptionUtils = Java.type('com.souf.karate.EncryptionUtils')
    * def encryption = new EncryptionUtils(encryptionEnv)
    * def dbConfig = { dbUsername: '#(dbUsername)', dbPassword: '#(dbPassword)', dbUrl: '#(dbUrl)', dbDriver: '#(dbDriver)' }
    * def DbUtils = Java.type('com.souf.karate.DbUtils')
    * def db = new DbUtils(dbConfig)
    * def kafkaConfig = { kafkaBrokers: '#(kafkaBrokers)', kafkaTopics: '#(kafkaTopics)' }
    * def KafkaUtils = Java.type('com.souf.karate.KafkaUtils')
    * callonce KafkaUtils.initializeConsumer(kafkaConfig)
    * eval sleep(15000)
    * callonce KafkaUtils.startConsuming()
    * eval sleep(15000)

  Scenario: AppA API 1 valid username and password
    Given url urlAppAapi1
    And request decodeRequest('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPnNvdWZwYXNzPC9wYXNzd29yZD4KPC9BcGlPbmVSZXF1ZXN0Pg==')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiOneResponse/status') == 'Y'
    * def tranId = responseHeaders['test_tran_id'][0]
    * print tranId
    * eval sleep(15000)
    * print db.readRows("select * from events where TRAN_ID = '" + tranId + "'")
    * def eventIds = db.readEvents(tranId)
    * print eventIds
    Then match eventIds == [1,2]
    * def tranKafkaTopics =  KafkaUtils.getTopicsInKafka(tranId)
    * def tranKafkaMessages = KafkaUtils.getMessagesInKafka(tranId)
    * def tranKafkaEventIds = KafkaUtils.readEvents(tranId)
    * print tranKafkaTopics
    * print tranKafkaMessages
    * print tranKafkaEventIds
    Then match tranKafkaTopics == [TopicEventAppAandAppB]
    Then match tranKafkaEventIds == [1,2]

  Scenario: AppA API 1 invalid username and valid password
    Given url urlAppAapi1
    And request decodeRequest('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+PC91c2VyTmFtZT4KICAgIDxwYXNzd29yZD5zb3VmcGFzczwvcGFzc3dvcmQ+CjwvQXBpT25lUmVxdWVzdD4=')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiOneResponse/status') == 'N'
    Then match karate.xmlPath(response, '/ApiOneResponse/userName') == 'Not a valid username'
    * def tranId = responseHeaders['test_tran_id'][0]
    * print tranId
    * eval sleep(15000)
    * print db.readRows("select * from events where TRAN_ID = '" + tranId + "'")
    * def eventIds = db.readEvents(tranId)
    * print eventIds
    Then match eventIds == [1,2]
    * def tranKafkaTopics =  KafkaUtils.getTopicsInKafka(tranId)
    * def tranKafkaMessages = KafkaUtils.getMessagesInKafka(tranId)
    * def tranKafkaEventIds = KafkaUtils.readEvents(tranId)
    * print tranKafkaTopics
    * print tranKafkaMessages
    * print tranKafkaEventIds
    Then match tranKafkaTopics == [TopicEventAppAandAppB]
    Then match tranKafkaEventIds == [1,2]

  Scenario: AppA API 1 valid username and invalid password
    Given url urlAppAapi1
    And request decodeRequest('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPjwvcGFzc3dvcmQ+CjwvQXBpT25lUmVxdWVzdD4=')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiOneResponse/status') == 'N'
    Then match karate.xmlPath(response, '/ApiOneResponse/userName') != 'Not a valid username'
    * def tranId = responseHeaders['test_tran_id'][0]
    * print tranId
    * eval sleep(15000)
    * print db.readRows("select * from events where TRAN_ID = '" + tranId + "'")
    * def eventIds = db.readEvents(tranId)
    * print eventIds
    Then match eventIds == [1,2]
    * def tranKafkaTopics =  KafkaUtils.getTopicsInKafka(tranId)
    * def tranKafkaMessages = KafkaUtils.getMessagesInKafka(tranId)
    * def tranKafkaEventIds = KafkaUtils.readEvents(tranId)
    * print tranKafkaTopics
    * print tranKafkaMessages
    * print tranKafkaEventIds
    Then match tranKafkaTopics == [TopicEventAppAandAppB]
    Then match tranKafkaEventIds == [1,2]

  Scenario: AppA API 1 invalid username and password
    Given url urlAppAapi1
    And request decodeRequest('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+PC91c2VyTmFtZT4KICAgIDxwYXNzd29yZD48L3Bhc3N3b3JkPgo8L0FwaU9uZVJlcXVlc3Q+')
    When method POST
    Then status 200
    Then match karate.xmlPath(response, '/ApiOneResponse/status') == 'N'
    Then match karate.xmlPath(response, '/ApiOneResponse/userName') == 'Not a valid username'
    * def tranId = responseHeaders['test_tran_id'][0]
    * print tranId
    * eval sleep(15000)
    * print db.readRows("select * from events where TRAN_ID = '" + tranId + "'")
    * def eventIds = db.readEvents(tranId)
    * print eventIds
    Then match eventIds == [1,2]
    * def tranKafkaTopics =  KafkaUtils.getTopicsInKafka(tranId)
    * def tranKafkaMessages = KafkaUtils.getMessagesInKafka(tranId)
    * def tranKafkaEventIds = KafkaUtils.readEvents(tranId)
    * print tranKafkaTopics
    * print tranKafkaMessages
    * print tranKafkaEventIds
    Then match tranKafkaTopics == [TopicEventAppAandAppB]
    Then match tranKafkaEventIds == [1,2]
