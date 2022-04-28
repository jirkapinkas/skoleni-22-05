package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Example2Component {

    @Bean
    public NewTopic outputAggTopic() {
        return TopicBuilder.name(Topics.OUTPUT_AGG_TOPIC)
                .partitions(20)
                .replicas(1)
                .build();
    }

}
