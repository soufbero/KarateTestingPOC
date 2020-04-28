package com.souf.karate.service;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
@ConditionalOnProperty(
        value="kafka.enabled",
        havingValue = "true")
public class KafkaService {

    @Value("${kafka.enabled}")
    private boolean kafkaEnabled;
    @Value("${kafka.brokers.dev}")
    private String kafkaBrokersDEV;
    @Value("${kafka.brokers.qa}")
    private String kafkaBrokersQA;
    @Value("${kafka.ssl.cert.dev}")
    private Resource kafkaSSLCertPathDEV;
    @Value("${kafka.ssl.pass.dev}")
    private String kafkaSSLCertPassDEV;
    @Value("${kafka.ssl.cert.qa}")
    private Resource kafkaSSLCertPathQA;
    @Value("${kafka.ssl.pass.qa}")
    private String kafkaSSLCertPassQA;

    private KafkaProducer<String,String> kafkaProducerClientDEV = null;
    private KafkaProducer<String,String> kafkaProducerClientQA = null;

    @PostConstruct
    private void initialize() throws Exception{
        if (kafkaEnabled){
            Properties props = new Properties();
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,"SSL");
            props.put(SslConfigs.SSL_PROTOCOL_CONFIG,"TLSv1.2");
            props.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG,"TLSv1.2");

            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaSSLCertPathDEV.getFile().getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLCertPathDEV.getFile().getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSSLCertPassDEV);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLCertPassDEV);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSSLCertPassDEV);
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersDEV);
            kafkaProducerClientDEV = new KafkaProducer<>(props);

            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaSSLCertPathQA.getFile().getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLCertPathQA.getFile().getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSSLCertPassQA);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLCertPassQA);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSSLCertPassQA);
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersQA);
            kafkaProducerClientQA = new KafkaProducer<>(props);
        }
    }

    public void sendToKafka(String topic, String tranId, String message, String env){
        if (kafkaEnabled){
            if (env.equals("DEV")){
                kafkaProducerClientDEV.send(new ProducerRecord<>(topic,tranId,message));
            }else if (env.equals("QA")){
                kafkaProducerClientQA.send(new ProducerRecord<>(topic,tranId,message));
            }
        }
    }
}
