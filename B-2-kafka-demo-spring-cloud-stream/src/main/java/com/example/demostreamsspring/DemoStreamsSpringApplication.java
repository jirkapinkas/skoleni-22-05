package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
//@EnableKafkaStreams // tohle vytvori bean typu StreamsBuilder
//@EnableSchemaRegistryClient
@SpringBootApplication
public class DemoStreamsSpringApplication {

//    @Primary
//    @Bean
//    public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.schema-registry-client.endpoint}") String endpoint) {
//        var client = new ConfluentSchemaRegistryClient();
//        client.setEndpoint(endpoint);
//        return client;
//    }

    // Tohle (+ konfigurace v application.properties) jednou za vterinu ulozi do topicu "orders.buy" novy zaznam
    @Bean
    public Supplier<Message<String>> orderBuySupplier() {
        return () -> {
                Message<String> o = MessageBuilder
                        .withPayload("test value")
                        .setHeader(KafkaHeaders.MESSAGE_KEY, "key1")
                        .build();
                log.info("Sent message to Kafka: {}", o.getPayload());
                return o;
        };
    }

    @Bean
    public Consumer<String> ordersConsumer(){
        return (value) -> log.info("Consumer Received : " + value);
    }


    public static void main(String[] args) {
        SpringApplication.run(DemoStreamsSpringApplication.class, args);
    }

}
