package com.example.demostreamsspring.controller;

import com.example.demostreamsspring.Stores;
import com.example.demostreamsspring.pojo.HostStoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Objects;

// inspirace:
// https://github.com/confluentinc/kafka-streams-examples/tree/7.0.0-post/src/main/java/io/confluent/examples/streams/interactivequeries
@Slf4j
@Profile("streams_app")
@RequiredArgsConstructor
@RestController
public class StateStoreController {

    private HostInfo hostInfo;

    private final RestTemplate restTemplate;

    @Value("${spring.kafka.streams.properties.application.server}")
    private String applicationServer;

    @PostConstruct
    public void init() {
        String[] strings = applicationServer.split(":");
        hostInfo = new HostInfo(strings[0], Integer.parseInt(strings[1]));
    }

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    // http://localhost:8181/temperatures/extreme/count/sensor_0
    // ...
    // http://localhost:8181/temperatures/extreme/count/sensor_9
    @GetMapping(path = "/temperatures/extreme/count/{sensorName}", produces = "application/json")
    public long temperaturesExtremeCount(@PathVariable String sensorName) {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        Objects.requireNonNull(kafkaStreams);

        HostStoreInfo hostStoreInfo = streamsMetadataForStoreAndKey(Stores.EXTREME_TEMPERATURES_COUNT, sensorName, new StringSerializer());
        log.info("{}", hostStoreInfo);
        if (!thisHost(hostStoreInfo)) {
            log.info("fetch remote data");
            return fetchByKey(hostStoreInfo, "temperatures/extreme/count/" + sensorName);
        }

        log.info("get local data");
        ReadOnlyKeyValueStore<Object, Object> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(Stores.EXTREME_TEMPERATURES_COUNT, QueryableStoreTypes.keyValueStore()));
        return (Long) store.get(sensorName);
    }

    private long fetchByKey(HostStoreInfo host, String path) {
        var url = String.format("http://%s:%d/%s", host.getHost(), host.getPort(), path);
        return restTemplate.getForObject(url, Long.class);
    }

    public <K> HostStoreInfo streamsMetadataForStoreAndKey(String store, K key, Serializer<K> keySerializer) {
        KafkaStreams streams = streamsBuilderFactoryBean.getKafkaStreams();
        Objects.requireNonNull(streams);
        KeyQueryMetadata metadata = streams.queryMetadataForKey(store, key, keySerializer);
        if (metadata == null) {
            throw new NullPointerException();
        }
        return new HostStoreInfo(metadata.activeHost().host(), metadata.activeHost().port(), Collections.singleton(store));
    }

    private boolean thisHost(HostStoreInfo host) {
        return host.getHost().equals(hostInfo.host()) && host.getPort() == hostInfo.port();
    }

}
