package com.gf.kafka_as400;

import org.springframework.core.env.Environment;

public class AppConfig {

    private static Environment _environment;

    public static void setEnvironment(Environment anEnvironment) {
        AppConfig._environment = anEnvironment;
    }

    
    public static String getAS400SystemName() {
        return _environment.getProperty("AS400.credential.system");
    }

    public static String getAS400Session() {
        return _environment.getProperty("AS400.credential.session");
    }

    public static String getAs400User() {
        return _environment.getProperty("AS400.credential.user");
    }

    public static String getAS400Password() {
        return _environment.getProperty("AS400.credential.password");
    }

    public static String getKafkaTopic() {
        return _environment.getProperty("kafka.TOPIC");
    }

    public static String getKafkaUsername() {
        return _environment.getProperty("kafka.username");
    }
    public static String getKafkaPassword() {
        return _environment.getProperty("kafka.password");
    }

    public static String getKafkaGroupdId() {
        return _environment.getProperty("kafka.groupdId");
    }

    public static String kafkaBootstrapServer() {
        return _environment.getProperty("kafka.bootstrap_server");
    }

    
}
