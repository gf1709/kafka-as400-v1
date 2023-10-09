package com.gf.kafka_as400;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class KafkaAS400Application {

    private static final Logger log = LoggerFactory.getLogger(KafkaAS400Application.class);
    

    public static void main(String[] args) throws IOException {
        SpringApplication.run(KafkaAS400Application.class, args);

 		log.info("Starting application.......");

		KAS400Service service = new KAS400Service();
		String ccsid =  service.getCCSID();
		log.info("ccsid is " + ccsid);

        service.callA02Pgm();

        log.info("Writing and Reading with Kafka...");
        KProducer.produce();
        KConsumer.consume();
        log.info("End Application");

    }
	
}
