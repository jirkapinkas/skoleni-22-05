package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class Example6StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);

        final Serde<CountAndSum> countAndSumValueSerde = new SpecificAvroSerde<>();
        countAndSumValueSerde.configure(serdeConfig, false);

        KStream<String, Temperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde));

        messageStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(10)))
//                .aggregate(() -> 0L, (key, value, aggregate) -> {
//                    log.info("called aggregate key:value:aggregate = {}:{}:{}", key, value, aggregate);
//                    return aggregate + 1L;
//                }, Materialized.with(Serdes.String(), Serdes.Long()))
                .count()
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .map((key, value) -> KeyValue.pair(key.key(), String.valueOf(value)))
                .to(Topics.TEMPERATURE_SUM_EVENTS_PER_MINUTE, Produced.with(Serdes.String(), Serdes.String()));

        messageStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(10)))
                .aggregate(() -> 0.0, (key, value, aggregate) -> aggregate + value.getVal(), Materialized.with(Serdes.String(), Serdes.Double()))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .map((key, value) -> KeyValue.pair(key.key(), String.valueOf(value)))
                .to(Topics.TEMPERATURE_SUM_TEMPERATURE_PER_MINUTE, Produced.with(Serdes.String(), Serdes.String()));


        messageStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(10)))
                .aggregate(CountAndSum::new,
                        (key, value, aggregate) -> {
                            aggregate.setCount(aggregate.getCount() + 1);
                            aggregate.setSum(aggregate.getSum() + value.getVal());
                            return aggregate;
                        }, Materialized.with(Serdes.String(), countAndSumValueSerde))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .mapValues((readOnlyKey, value) -> value.getSum() / value.getCount())
                .mapValues((readOnlyKey, value) -> String.valueOf(value))
                .map((key, value) -> KeyValue.pair(key.key(), String.valueOf(value)))
                // Tyhle dva radky jsou kdyz nas zajima posledni hodnota => ulozime ji do table a store a pak ji muzeme ze store ziskat
                // Pokud neni zapotrebi ukladat do table + store, pak neni zapotrebi transformace do Table a pak zpet do Streamu
                .toTable(Materialized.<String, String, KeyValueStore < Bytes, byte[]>>as(Stores.AVERAGE_TEMPERATURE_PER_MINUTE)
                        .withKeySerde(Serdes.String()).withValueSerde(Serdes.String()))
                .toStream()
                .to(Topics.TEMPERATURE_AVERAGE_TEMPERATURE_PER_MINUTE, Produced.with(Serdes.String(), Serdes.String()));

        // Vypise topologii do konzole
        // https://zz85.github.io/kafka-streams-viz/
        Topology topology = streamsBuilder.build();
        log.info("Topology description {}", topology.describe());

    }

}