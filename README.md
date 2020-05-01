# Karate Testing POC

This POC provides an example of Karate testing framework integrated with RDBMS, Kafka, 
SSL secured APIs, and few other features.


## Modules Breakdown

The following project has 6 modules:
* **AppA**: The first application to be tested.
* **AppB**: The second application to be tested.
* **AppC**: The third application to be tested.
* **AppsCommon**: Common module between AppA, AppB, and AppC.
* **AppsTesting**: Module with Karate test cases implementation and configuration.
* **KafkaTestConsumer**: Example consumer for data in Kafka.

Each application (AppA/AppB/AppC) provides Rest APIs to execute business logic. 
These APIs are prefixed with either _/dev_ or _/qa_ path to mimic multiple environments testing.
All applications are integrated with an RDBMS (MySQL) and Kafka. 
To disable this integration, use the following flags in **application.properties** file:

```
db.enabled=true

kafka.enabled=true
```


## Dependencies Setup

As previously mentioned, each application has an optional integration with MySQL and Kafka.
If you are interested in investigating Karate's potential with either of these technologies, 
you will need to perform the initial setup.

**Note:** these setup steps have been created on Windows 10 workstation. Please adjust accordingly to fit your system.

### MySQL Setup:

1- Download and install MySQL Server from [Download MySQL](https://dev.mysql.com/downloads/)

2- Login with Root account and create the following 6 databases. 
Each application has 2 databases; 1 for DEV environment and second for QA.
```
CREATE DATABASE appadevdb;
CREATE DATABASE appaqadb;
CREATE DATABASE appbdevdb;
CREATE DATABASE appbqadb;
CREATE DATABASE appcdevdb;
CREATE DATABASE appcqadb;
```

3- Create a User and grant it all Privileges on the databases.
```
CREATE USER 'dbuser'@'%' IDENTIFIED BY 'dbpassword';

GRANT ALL PRIVILEGES ON appadevdb.* TO 'dbuser'@'%';
GRANT ALL PRIVILEGES ON appaqadb.* TO 'dbuser'@'%';
GRANT ALL PRIVILEGES ON appbdevdb.* TO 'dbuser'@'%';
GRANT ALL PRIVILEGES ON appbqadb.* TO 'dbuser'@'%';
GRANT ALL PRIVILEGES ON appcdevdb.* TO 'dbuser'@'%';
GRANT ALL PRIVILEGES ON appcqadb.* TO 'dbuser'@'%'; 
```

4- Create tables based on the following database breakdown:

Application  | Databases              | Tables
------------ | ---------------------- | ------------
AppA         | appadevdb and appaqadb | EVENTS
AppB         | appbdevdb and appbqadb | EVENTS and LOGIN_REQUESTS
AppC         | appcdevdb and appcqadb | EVENTS, LOGIN_REQUESTS, and BAD_LOGINS

The table creation queries for each table are as follows:
* EVENTS
```
drop table if exists EVENTS;
create table EVENTS(
   SEQ_ID INT NOT NULL AUTO_INCREMENT,
   TRAN_ID VARCHAR(100) NOT NULL,
   EVT_ID INT NOT NULL,
   MESSAGE_TXT VARCHAR(400),
   TRAN_TS TIMESTAMP NOT NULL,
   INSERT_TS TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY ( SEQ_ID )
);
```
* LOGIN_REQUESTS
```
drop table if exists LOGIN_REQUESTS;
create table LOGIN_REQUESTS(
   SEQ_ID INT NOT NULL AUTO_INCREMENT,
   TRAN_ID VARCHAR(100) NOT NULL,
   USERNAME VARCHAR(40),
   PASSWORD VARCHAR(40),
   LOGIN_INFO VARCHAR(400) NOT NULL,
   TRAN_TS TIMESTAMP NOT NULL,
   INSERT_TS TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY ( SEQ_ID )
);
```
* BAD_LOGINS
```
drop table if exists BAD_LOGINS;
create table BAD_LOGINS(
   SEQ_ID INT NOT NULL AUTO_INCREMENT,
   TRAN_ID VARCHAR(100) NOT NULL,
   REASON VARCHAR(400) NOT NULL,
   USERNAME VARCHAR(40),
   TRAN_TS TIMESTAMP NOT NULL,
   INSERT_TS TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY ( SEQ_ID )
);
```

### Kafka Setup:

1- [Download Kafka](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.4.0/kafka_2.13-2.4.0.tgz)

2- untar archive. 

**Note:** For the upcoming steps, the paths of commands are based on C:\Kafka\Karate\kafka_2.13-2.4.0 being the Kafka
installation path. So modify any command that specifies that path based on your actual Kafka path.

3- Go to config folder in C:\Kafka\Karate\kafka_2.13-2.4.0\config

4- Create two copies in the same folder of zookeeper.properties and call them 
zookeeperDEV.properties and zookeeperQA.properties

5- Create two copies in the same folder of server.properties call them 
serverDEV.properties and serverQA.properties

6- Copy **KarateKafkaDev.jks** and **KarateKafkaQa.jks** files to C:\Kafka\Karate 
from the **resources/certs/kafka** folder in **AppsCommon** module.

7- Modify each of the four files to have these values added/updated:

* zookeeperDEV.properties
```
dataDir=C:/Kafka/Karate/kafka_2.13-2.4.0/data/zookeeper-dev
clientPort=2181
```
* zookeeperQA.properties
```
dataDir=C:/Kafka/Karate/kafka_2.13-2.4.0/data/zookeeper-qa
clientPort=2182
```
* serverDEV.properties
```
listeners=SSL://localhost:9094
ssl.keystore.location=C:/Kafka/Karate/KarateKafkaDev.jks
ssl.keystore.password=karatetestingcertpass
ssl.key.password=karatetestingcertpass
ssl.truststore.location=C:/Kafka/Karate/KarateKafkaDev.jks
ssl.truststore.password=karatetestingcertpass
ssl.enabled.protocols=TLSv1.2
ssl.client.auth=required
security.inter.broker.protocol=SSL

log.dirs=C:/Kafka/Karate/kafka_2.13-2.4.0/data/broker-dev

zookeeper.connect=localhost:2181
```
* serverQA.properties
```
listeners=SSL://localhost:9093
ssl.keystore.location=C:/Kafka/Karate/KarateKafkaQa.jks
ssl.keystore.password=karatetestingcertpass
ssl.key.password=karatetestingcertpass
ssl.truststore.location=C:C:/Kafka/Karate/KarateKafkaQa.jks
ssl.truststore.password=karatetestingcertpass
ssl.enabled.protocols=TLSv1.2
ssl.client.auth=required
security.inter.broker.protocol=SSL

log.dirs=C:/Kafka/Karate/kafka_2.13-2.4.0/data/broker-qa

zookeeper.connect=localhost:2182
```

8- Start 4 terminals and on each, browse to path C:\Kafka\Karate\kafka_2.13-2.4.0

9- Start ZooKeeper and Kafka Brokers in this order in the four terminals:
* Terminal 1
```
bin\windows\zookeeper-server-start.bat config\zookeeperDEV.properties
```
* Terminal 2
```
bin\windows\kafka-server-start.bat config\serverDEV.properties
```
* Terminal 3
```
bin\windows\zookeeper-server-start.bat config\zookeeperQA.properties
```
* Terminal 4
```
bin\windows\kafka-server-start.bat config\serverQA.properties
```

**Note:** Every time you want to start the two Kafka clusters, repeat steps 8 and 9. Steps 10, 11, and 12 
are only done for initial setup.

10- Start a 5th terminal and browse to path C:\Kafka\Karate\kafka_2.13-2.4.0 

11- Create the Topics needed by running the following commands:
```
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicEventAppAandAppB
bin\windows\kafka-topics.bat --create --zookeeper localhost:2182 --replication-factor 1 --partitions 1 --topic TopicEventAppAandAppB
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicLoginAppB
bin\windows\kafka-topics.bat --create --zookeeper localhost:2182 --replication-factor 1 --partitions 1 --topic TopicLoginAppB
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicEventAppC
bin\windows\kafka-topics.bat --create --zookeeper localhost:2182 --replication-factor 1 --partitions 1 --topic TopicEventAppC
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicLoginAppC
bin\windows\kafka-topics.bat --create --zookeeper localhost:2182 --replication-factor 1 --partitions 1 --topic TopicLoginAppC
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicBadLoginAppC
bin\windows\kafka-topics.bat --create --zookeeper localhost:2182 --replication-factor 1 --partitions 1 --topic TopicBadLoginAppC
```

12- Validate that each of the Kafka Clusters (DEV and QA) has all the 5 Topics:
```
bin\windows\kafka-topics.bat --list --zookeeper localhost:2181

bin\windows\kafka-topics.bat --list --zookeeper localhost:2182
```

## Validate Applications
After your chosen dependencies are setup (MySQL/Kafka), the applications (AppA/AppB/AppC) should start without issues.
All paths for SSL certificates are relative and applications are running on different ports, so no additional setup 
should be required. 

You can post test requests using the included JKS/PFX file (KarateApiDevQA.jks/KarateApiDevQA.pfx) in **AppsCommon** 
_resources/certs/api_ folder
and validate that data is stored in DB and/or Kafka (you can use **KafkaTestConsumer** module for the latter).



## Testing
### First Test and Reports:
Browse to **AppsTesting** module and run:
```
mvn clean test -Denv=dev -Dapps=AppA,AppB,AppC -Dsmoke=true -Ddb=true -Dkafka=true -Dencryption=true
```
Multiple reports will be generated in the target folder with the main one being under:
```
AppsTesting/target/cucumber-html-reports/overview-features.html
```


### Testing Options:
When running a test, the following options are available:
* **-Denv**: can be either **dev** or **qa**
* **-Dapps**: tester can choose which applications to test in a comma separated list.
* **-Dsmoke**: can be either **true** or **false**. When true, **Smoke.feature** test file is run fist successfully 
before actual applications testings starts.
* **-Ddb**: can be either **true** or **false**. Toggle validating DB integration.
* **-Dkafka**: can be either **true** or **false**. Toggle validating Kafka integration.
* **-Dencryption**: can be either **true** or **false**. Toggle validating Encryption in DB/Kafka.

These options update configuration values in **karate-config.js** file before tests begin. 
**_feature_** files then use those values to decide which tests to run and which to skip. 


### Testing Flow:
1- The command **mvn clean test** scans the **AppsTesting** module for any java classes in the **test** folder 
and finds **ApplicationTests.java**

2- Karate testing framework will load all the testing options (JVM arguments) to **karate-config.js** and 
update the config values which the **feature** files are able to use.

3- The method annotated with **@BeforeAll** will run first. This method runs **PreTests.feature** file to initialize
integration classes for testing. After that, if smoke testing option is enabled, **Smoke.feature** file will run 
using tags based on the **apps** jvm argument. Smoke tests are executed in parallel.

4- If step 3 is completed successfully, the method annotated with **@Test** will run the test cases in
**AppA.feature** and/or **AppB.feature** and/or **AppC.feature** based on the **apps** jvm argument. 
Application tests are executed in parallel at the **Feature** file and **Scenario** levels.

5- Reports are generated in **target** folder.

## Authors

* **Soufiane Berouel**
