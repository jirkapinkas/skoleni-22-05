package com.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class MainConsumer {

    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<>();

        props.put("bootstrap.servers", "pkc-4ygn6.europe-west3.gcp.confluent.cloud:9092");
        props.put("group.id", "group_ID");
//        props.put("group.id", "group_ID_" + UUID.randomUUID());
//        props.put("auto.offset.reset", "earliest"); // tohle plus random group.id je to same jako --from-beginning
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");


        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule   required username='TJPXSFM5BTJQBXUA'   password='IjwvCbCuclz4viFCq2EZXwtt/z/z/KtDL4bKwvLMm4BbMAnVg3/XEfChQZXrsPfw';");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(List.of("first_topic"));

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
            for (ConsumerRecord<String, String> record : records) {
                log.info("received: {} {} {} ", record.offset(), record.key(), record.value());
            }
        }
    }

}
