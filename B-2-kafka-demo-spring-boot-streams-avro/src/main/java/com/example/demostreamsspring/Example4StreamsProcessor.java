package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
//@Component
public class Example4StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);

        KStream<String, Temperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde));

        messageStream
                .filter((key, temperature) -> temperature.getVal() > 98)
                .groupByKey() // provede se sgrupovani dat podle klice
                .count(Materialized.as(Stores.EXTREME_TEMPERATURES_COUNT)) // data se ulozi do store s nazvem "extremeTemperaturesCount"
                .toStream()
                .to(Topics.EXTREME_TEMPERATURES, Produced.with(Serdes.String(), Serdes.Long()));

        // Stav store se zpristupnuje a vraci v StateStoreController

        messageStream
                .groupByKey()
                // najde maximalni hodnotu
                .reduce((value1, value2) -> {
                    if(value1.getVal() < value2.getVal()) {
                        return value2;
                    } else {
                        return value1;
                    }
                }, Materialized.as(Stores.MAX_TEMPERATURE)) // data se ulozi do store s nazvem "maxTemperature"
                .toStream()
                .to(Topics.MAX_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));
    }

}