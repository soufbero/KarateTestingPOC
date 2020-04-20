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
        dbUrl: '',
        dbUsername: '',
        dbPassword: '',
        dbDriver: ''
    };

    if (env === 'dev') {
        config.urlAppAapi1 = 'http://localhost:8080/dev/api1';
        config.urlAppBapi1 = 'http://localhost:8081/dev/api1';
        config.urlAppBapi2 = 'http://localhost:8081/dev/api2';
        config.urlAppCapi1 = 'http://localhost:8083/dev/api1';
        if (db === 'true'){
            karate.log('Got HERE 1111');
            config.dbUrl = 'jdbc:mysql://localhost:3306/appadevdb';
            config.dbUsername = 'dbuser';
            config.dbPassword = 'dbpassword';
            config.dbDriver = 'com.mysql.cj.jdbc.Driver';
        }
    } else if (env === 'qa') {
        config.urlAppAapi1 = 'http://localhost:8080/qa/api1';
        config.urlAppBapi1 = 'http://localhost:8081/qa/api1';
        config.urlAppBapi2 = 'http://localhost:8081/qa/api2';
        config.urlAppCapi1 = 'http://localhost:8083/qa/api1';
        if (db === 'true'){
            config.dbUrl = 'jdbc:mysql://localhost:3306/appaqadb';
            config.dbUsername = 'dbuser';
            config.dbPassword = 'dbpassword';
            config.dbDriver = 'com.mysql.cj.jdbc.Driver';
        }
    }

    return config;
}
