package com.example.demostreamsspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.web.client.RestTemplate;

@Profile("streams_app")
@Configuration
@EnableKafkaStreams // tohle vytvori bean typu StreamsBuilder
public class StreamsConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
