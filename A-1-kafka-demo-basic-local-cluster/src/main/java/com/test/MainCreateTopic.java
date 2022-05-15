package com.test;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.*;

public class MainCreateTopic {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

        try(AdminClient admin = AdminClient.create(config)) {

            Map<String, String> configs = new HashMap<>();
            int partitions = 5;
            short replication = 2;

            admin.createTopics(List.of(new NewTopic("first_topic", partitions, replication).configs(configs)));
        }
    }

}
