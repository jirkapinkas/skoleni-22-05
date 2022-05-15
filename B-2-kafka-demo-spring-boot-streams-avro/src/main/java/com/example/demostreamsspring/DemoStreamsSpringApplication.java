package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@EnableKafkaStreams // tohle vytvori bean typu StreamsBuilder
@EnableSchemaRegistryClient
@SpringBootApplication
public class DemoStreamsSpringApplication {

    @Primary
    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.schema-registry-client.endpoint}") String endpoint) {
        var client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoStreamsSpringApplication.class, args);
    }

}
