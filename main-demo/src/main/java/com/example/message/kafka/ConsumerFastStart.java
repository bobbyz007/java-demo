package com.example.message.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerFastStart {
    public static final String BOOTSTRAP_SERVER = "localhost:9092";
    public static final String TOPIC = "topic-demo";
    public static final String GROUP_ID = "group.demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("bootstrap.servers", BOOTSTRAP_SERVER);
        properties.put("group.id", GROUP_ID);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        // 订阅主题
        consumer.subscribe(Collections.singletonList(TOPIC));

        // 循环消费消息
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
            }
        }
    }
}
