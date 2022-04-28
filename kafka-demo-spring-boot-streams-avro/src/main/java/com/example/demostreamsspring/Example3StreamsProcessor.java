package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
//@Component
public class Example3StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);

        KStream<String, Temperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde));

        messageStream
                .filter((key, temperature) -> temperature.getVal() < 10)
                .to(Topics.LOW_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

        messageStream
                .filter((key, temperature) -> temperature.getVal() > 90)
                .to(Topics.HIGH_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

        // NEBO TO SAME POMOCI BRANCH (Poznamka: Pouzil jsem novy zpusob zapisu branch, ktery je od Kafka 2.8.0 (od zacatku roku 2021)):
//        messageStream
//                .split()
//                .branch((key, value) -> value.getVal() < 10, Branched.withConsumer(stringTemperatureKStream -> stringTemperatureKStream.to(Topics.LOW_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde))))
//                .branch((key, value) -> value.getVal() > 90, Branched.withConsumer(stringTemperatureKStream -> stringTemperatureKStream.to(Topics.HIGH_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde))))
//                .noDefaultBranch();

        // STARYM ZPUSOBEM POMOCI BRANCH (od Kafka 2.8.0 deprecated):
//        KStream<String, Temperature>[] branch = messageStream
//                .branch((key, value) -> value.getVal() < 10, (key, value) -> value.getVal() > 90);
//        branch[0].to(Topics.LOW_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));
//        branch[1].to(Topics.HIGH_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

    }

}