package com.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class MainProducer {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> props = new HashMap<>();

        //Assign localhost id
        props.put("bootstrap.servers", "pkc-4ygn6.europe-west3.gcp.confluent.cloud:9092");
        //Set acknowledgements for producer requests.
        props.put("acks", "all");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);
        //Specify buffer size in config
        props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule   required username='TJPXSFM5BTJQBXUA'   password='IjwvCbCuclz4viFCq2EZXwtt/z/z/KtDL4bKwvLMm4BbMAnVg3/XEfChQZXrsPfw';");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        while(true) {
            var value = UUID.randomUUID().toString();
            producer.send(new ProducerRecord<>("first_topic", "test_key", value));
            log.info("sent: {}", value);
            Thread.sleep(1_000);
        }
//        producer.flush();
//        producer.close();
    }

}
