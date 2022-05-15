package com.example.demostreamsspring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Profile("streams_app")
@Configuration
@EnableKafkaStreams // tohle vytvori bean typu StreamsBuilder
public class StreamsConfiguration {
}
