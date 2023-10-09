package com.gf.kafka_as400;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MyKafkaAS400Application {

    private static final Logger log = LoggerFactory.getLogger(MyKafkaAS400Application.class);
    

    public static void main(String[] args) throws IOException {
        SpringApplication.run(MyKafkaAS400Application.class, args);

 		log.info("Starting application.......");

		MyAS400Service service = new MyAS400Service();
		String ccsid =  service.getCCSID();
		log.info("ccsid is " + ccsid);

        service.callA02Pgm();

        log.info("Writing and Reading with Kafka...");
        MyKafkaProducer.produce();
        MyKafkaConsumer.consume();
        log.info("End Application");

    }
	
}
