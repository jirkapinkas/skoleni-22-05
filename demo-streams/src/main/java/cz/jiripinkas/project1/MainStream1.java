package cz.jiripinkas.project1;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class MainStream1 {

    public static void main(String[] args) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-sensor-A-application"); // tohle je jako group.id
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "stream-sensor-A-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 10);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class); // Misto Temperature se musi pouzivat GenericRecord
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class); // Misto GenericRecord se musi pouzivat Temperature
        streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

        // https://docs.confluent.io/platform/current/streams/developer-guide/datatypes.html#avro
        final StreamsBuilder builder = new StreamsBuilder();

        Serde<Temperature> valueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        valueSerde.configure(serdeConfig, false);

        final KStream<String, Temperature> stream = builder
//                .stream("sensor_A", Consumed.with(Serdes.String(), valueSerde)); // custom serde pro key & value
                .stream("sensor_A"); // serde se bere vychozi (definovane ve streamsConfiguration)
                stream
                        .filter((s, temperature) -> {
                            double val = temperature.getVal();
                            return val > 90.0;
                        })
                        .mapValues((key, temperature) -> temperature.getVal())
                        .to("sensor_A_filtered_over_90", Produced.with(Serdes.String(), Serdes.Double()));
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
