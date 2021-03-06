function fn() {
    var env = karate.properties['env'];
    var apps = karate.properties['apps'];
    var db = karate.properties['db'];
    var kafka = karate.properties['kafka'];
    var encryption = karate.properties['encryption'];

    var config = {
        urlAppAapi1: '', urlAppBapi1: '', urlAppBapi2: '', urlAppCapi1: '',
        validateDB:false,
        dbUrlAppA: '', dbUsernameAppA: '', dbPasswordAppA: '', dbDriverAppA: '',
        dbUrlAppB: '', dbUsernameAppB: '', dbPasswordAppB: '', dbDriverAppB: '',
        dbUrlAppC: '', dbUsernameAppC: '', dbPasswordAppC: '', dbDriverAppC: '',
        validateKafka:false, kafkaBrokers:'', kafkaTopics:'', kafkaCertPath:'', kafkaCertPass:'',
        kafkaTopic1:'', kafkaTopic2:'', kafkaTopic3:'', kafkaTopic4:'', kafkaTopic5:'',
        validateEncryption:false, encryptionEnv:''
    };

    if (env === 'dev') {
        config.urlAppAapi1 = 'https://localhost:8080/dev/api1';
        config.urlAppBapi1 = 'https://localhost:8081/dev/api1';
        config.urlAppBapi2 = 'https://localhost:8081/dev/api2';
        config.urlAppCapi1 = 'https://localhost:8083/dev/api1';
        if (db === 'true'){
            config.validateDB = true;
            config.dbUrlAppA = 'jdbc:mysql://localhost:3306/appadevdb';
            config.dbUsernameAppA = 'dbuser';
            config.dbPasswordAppA = 'dbpassword';
            config.dbDriverAppA = 'com.mysql.cj.jdbc.Driver';
            config.dbUrlAppB = 'jdbc:mysql://localhost:3306/appbdevdb';
            config.dbUsernameAppB = 'dbuser';
            config.dbPasswordAppB = 'dbpassword';
            config.dbDriverAppB = 'com.mysql.cj.jdbc.Driver';
            config.dbUrlAppC = 'jdbc:mysql://localhost:3306/appcdevdb';
            config.dbUsernameAppC = 'dbuser';
            config.dbPasswordAppC = 'dbpassword';
            config.dbDriverAppC = 'com.mysql.cj.jdbc.Driver';
        }
        if (kafka === 'true'){
            config.validateKafka = true;
            config.kafkaBrokers = 'localhost:9094';
            config.kafkaCertPath = 'certs/KarateKafkaDev.jks';
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
            config.dbUrlAppA = 'jdbc:mysql://localhost:3306/appaqadb';
            config.dbUsernameAppA = 'dbuser';
            config.dbPasswordAppA = 'dbpassword';
            config.dbDriverAppA = 'com.mysql.cj.jdbc.Driver';
            config.dbUrlAppB = 'jdbc:mysql://localhost:3306/appbqadb';
            config.dbUsernameAppB = 'dbuser';
            config.dbPasswordAppB = 'dbpassword';
            config.dbDriverAppB = 'com.mysql.cj.jdbc.Driver';
            config.dbUrlAppC = 'jdbc:mysql://localhost:3306/appcqadb';
            config.dbUsernameAppC = 'dbuser';
            config.dbPasswordAppC = 'dbpassword';
            config.dbDriverAppC = 'com.mysql.cj.jdbc.Driver';
        }
        if (kafka === 'true'){
            config.validateKafka = true;
            config.kafkaBrokers = 'localhost:9093';
            config.kafkaCertPath = 'certs/KarateKafkaQa.jks';
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

    karate.configure('ssl', {
        trustAll: true,
        keyStorePassword: 'karatetestingcertpass',
        keyStoreType: 'pkcs12',
        keyStore: 'classpath:certs/KarateApiDevQA.pfx'}
        );

    return config;
}
