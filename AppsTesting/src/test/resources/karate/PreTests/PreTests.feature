Feature: Pre-Tests Setup

  Scenario: Setup Integration Points
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * def dbConfig = { user: '#(dbUsername)', pass: '#(dbPassword)', url: '#(dbUrl)', driver: '#(dbDriver)' }
    * def kafkaConfig = { brokers: '#(kafkaBrokers)', topics: '#(kafkaTopics)', certPath: '#(kafkaCertPath)', certPass: '#(kafkaCertPass)' }
    * def DbUtils = Java.type('com.souf.karate.DbUtils')
    * def EncodingUtils = Java.type('com.souf.karate.EncodingUtils')
    * def EncryptionUtils = Java.type('com.souf.karate.EncryptionUtils')
    * def KafkaUtils = Java.type('com.souf.karate.KafkaUtils')
    * def OtherUtils = Java.type('com.souf.karate.OtherUtils')
    * OtherUtils.setValidationFlags(validateDB,validateKafka,validateEncryption)
    * EncodingUtils.initialize()
    * EncryptionUtils.initialize(encryptionEnv)
    * DbUtils.initialize(dbConfig)
    * KafkaUtils.initialize(kafkaConfig)
    * if (validateKafka == true) sleep(15000)
    * if (validateKafka == true) KafkaUtils.startConsuming()
    * if (validateKafka == true) sleep(15000)
