function fn() {
    var env = karate.properties['env'];
    var apps = karate.properties['apps'];
    var db = karate.properties['db'];
    var kafka = karate.properties['kafka'];
    var encryption = karate.properties['encryption'];

    var config = {
        urlAppAapi1: '',
        urlAppBapi1: '',
        urlAppBapi2: '',
        urlAppCapi1: '',
        validateDB:false,
        dbUrl: '',
        dbUsername: '',
        dbPassword: '',
        dbDriver: '',
        validateKafka:false,
        kafkaBrokers:'',
        kafkaTopics:'',
        kafkaTopic1:'',
        kafkaTopic2:'',
        kafkaTopic3:'',
        kafkaTopic4:'',
        kafkaTopic5:'',
        kafkaCertPath:'',
        kafkaCertPass:'',
        validateEncryption:false,
        encryptionEnv:''
    };

    if (env === 'dev') {
        config.urlAppAapi1 = 'https://localhost:8080/dev/api1';
        config.urlAppBapi1 = 'https://localhost:8081/dev/api1';
        config.urlAppBapi2 = 'https://localhost:8081/dev/api2';
        config.urlAppCapi1 = 'https://localhost:8083/dev/api1';
        if (db === 'true'){
            //karate.log('Got HERE');
            config.validateDB = true;
            config.dbUrl = 'jdbc:mysql://localhost:3306/appadevdb';
            config.dbUsername = 'dbuser';
            config.dbPassword = 'dbpassword';
            config.dbDriver = 'com.mysql.cj.jdbc.Driver';
        }
        if (kafka === 'true'){
            config.validateKafka = true;
            config.kafkaBrokers = 'localhost:9094';
            config.kafkaCertPath = 'C:/Users/soufi/Desktop/Soufiane/dev/KarateTestingPOC/KarateKafkaDev.jks';
            config.kafkaCertPass = 'karatetestingcertpass';
            if (apps.contains('AppA') && !apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB';
            }else if (!apps.contains('AppA') && apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB';
            }else if (!apps.contains('AppA') && !apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (apps.contains('AppA') && apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB';
            }else if (apps.contains('AppA') && !apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (!apps.contains('AppA') && apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (apps.contains('AppA') && apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }
            config.kafkaTopic1 = 'TopicEventAppAandAppB';
            config.kafkaTopic2 = 'TopicLoginAppB';
            config.kafkaTopic3 = 'TopicEventAppC';
            config.kafkaTopic4 = 'TopicLoginAppC';
            config.kafkaTopic5 = 'TopicBadLoginAppC';
        }
        if (encryption === 'true'){
            config.validateEncryption = true;
            config.encryptionEnv = "DevEncryption"
        }
    } else if (env === 'qa') {
        config.urlAppAapi1 = 'https://localhost:8080/qa/api1';
        config.urlAppBapi1 = 'https://localhost:8081/qa/api1';
        config.urlAppBapi2 = 'https://localhost:8081/qa/api2';
        config.urlAppCapi1 = 'https://localhost:8083/qa/api1';
        if (db === 'true'){
            config.validateDB = true;
            config.dbUrl = 'jdbc:mysql://localhost:3306/appaqadb';
            config.dbUsername = 'dbuser';
            config.dbPassword = 'dbpassword';
            config.dbDriver = 'com.mysql.cj.jdbc.Driver';
        }
        if (kafka === 'true'){
            config.validateKafka = true;
            config.kafkaBrokers = 'localhost:9093';
            config.kafkaCertPath = 'C:/Users/soufi/Desktop/Soufiane/dev/KarateTestingPOC/KarateKafkaQa.jks';
            config.kafkaCertPass = 'karatetestingcertpass';
            if (apps.contains('AppA') && !apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB';
            }else if (!apps.contains('AppA') && apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB';
            }else if (!apps.contains('AppA') && !apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (apps.contains('AppA') && apps.contains('AppB') && !apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB';
            }else if (apps.contains('AppA') && !apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (!apps.contains('AppA') && apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }else if (apps.contains('AppA') && apps.contains('AppB') && apps.contains('AppC')){
                config.kafkaTopics = 'TopicEventAppAandAppB,TopicLoginAppB,TopicEventAppC,TopicLoginAppC,TopicBadLoginAppC';
            }
            config.kafkaTopic1 = 'TopicEventAppAandAppB';
            config.kafkaTopic2 = 'TopicLoginAppB';
            config.kafkaTopic3 = 'TopicEventAppC';
            config.kafkaTopic4 = 'TopicLoginAppC';
            config.kafkaTopic5 = 'TopicBadLoginAppC';
        }
        if (encryption === 'true'){
            config.validateEncryption = true;
            config.encryptionEnv = "QaEncryption"
        }
    }

    karate.configure('ssl', {trustAll: true, keyStorePassword: 'karatetestingcertpass', keyStoreType: 'pkcs12', keyStore: 'C:/Users/soufi/Desktop/Soufiane/dev/KarateTestingPOC/KarateApiDevQA.pfx'});

    return config;
}
