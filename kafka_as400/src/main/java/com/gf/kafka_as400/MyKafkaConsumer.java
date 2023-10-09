package com.gf.kafka_as400;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyKafkaConsumer {
	private static final Logger log = LoggerFactory.getLogger(MyKafkaConsumer.class);

	public static void consume() throws IOException {

		final String TOPIC = AppConfig.getKafkaTopic();
		final String username = AppConfig.getKafkaUsername();
		final String password = AppConfig.getKafkaPassword();
		final String bootstrapServer = AppConfig.kafkaBootstrapServer();
		final String groupdId = AppConfig.getKafkaGroupdId();

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupdId);
		props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
		props.put(SaslConfigs.SASL_JAAS_CONFIG,
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username
						+ "\" password=\"" + password + "\";");

		// props.put("auto.offset.reset", "earliest");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		// props.put("enable.commit.interval.ms", "1000");

		try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(TOPIC));
			for (int i = 0; i < 2; i++) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
				log.info("Polling...");

				if (records.count() > 0) {
					log.info(String.format("Record changed : %d   ---------------->", records.count()));
					for (ConsumerRecord<String, String> record : records) {
						log.info(String.format("received message: key:%s, value:%s", record.key(), record.value()));
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not start consumer: " + e);
		}
	}
}
