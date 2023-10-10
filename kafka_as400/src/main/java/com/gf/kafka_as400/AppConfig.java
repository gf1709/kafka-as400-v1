package com.gf.kafka_as400;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.ibm.as400.access.QSYSObjectPathName;

public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

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

    public static String getKafkaBootstrapServer() {
        return _environment.getProperty("kafka.bootstrap_server");
    }

    public static void printConfig() {
        log.info(" ");
        log.info("AS400 system.................." + getAS400SystemName());
        log.info("AS400 session................." + getAS400Session());
        log.info("AS400 user...................." + getAs400User());
        log.info("AS400 password................" + getAS400Password());
        log.info(" ");
        log.info("Kafka bootstrap server........" + getKafkaBootstrapServer());
        log.info("Kafka Topic..................." + getKafkaTopic());
        log.info("Kafka username................" + getKafkaUsername());
        log.info("Kafka password................" + getKafkaPassword());
        log.info("Kafka group id................" + getKafkaGroupdId());
        log.info(" ");
    }

}
