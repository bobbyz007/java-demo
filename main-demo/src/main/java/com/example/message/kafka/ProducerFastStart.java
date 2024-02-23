package com.example.message.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ProducerFastStart {
    public static final String BOOTSTRAP_SERVER = "localhost:9092";
    public static final String TOPIC = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", BOOTSTRAP_SERVER);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try {
            for (int i = 0; i < 60; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "hello, kafka! " + i);
                producer.send(record);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        producer.close();
    }
}
