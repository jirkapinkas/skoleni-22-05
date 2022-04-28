package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Example3Component {

    @Bean
    public NewTopic lowTemperaturesTopic() {
        return TopicBuilder.name(Topics.LOW_TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic highTemperaturesTopic() {
        return TopicBuilder.name(Topics.HIGH_TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

}
