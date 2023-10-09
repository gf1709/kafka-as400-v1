package com.gf.kafka_as400;

import java.sql.Timestamp;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyKafkaProducer {
	private static final Logger log = LoggerFactory.getLogger(MyKafkaConsumer.class);

	public static void produce() {

		final String TOPIC = AppConfig.getKafkaTopic();
		final String username = AppConfig.getKafkaUsername();
		final String password = AppConfig.getKafkaPassword();
		final String bootstrapServer = AppConfig.kafkaBootstrapServer();

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
		props.put(SaslConfigs.SASL_JAAS_CONFIG,
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username
						+ "\" password=\"" + password + "\";");

		String key = "k1";
		String val = "ciao Greg " + new Timestamp(System.currentTimeMillis());
		final Producer<String, String> producer = new KafkaProducer<>(props);
		try {
			producer.send(new ProducerRecord<>(TOPIC, key, val),
					(event, ex) -> {
						if (ex != null)
							ex.printStackTrace();
						else
							log.info(String.format("Produced event to topic %s: key = %-10s value = %s%n", TOPIC, key,
									val));
					});

			producer.flush();
		} catch (Exception e) {
			log.error(String.format("Eccezione in fase di spedizione %s: key = %-10s value = %s%n", TOPIC, key, val));
		} finally {
			producer.close();
		}
	}
}
