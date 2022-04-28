package com.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MainListTopics {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

        try(AdminClient admin = AdminClient.create(config)) {
            ListTopicsResult listTopicsResult = admin.listTopics();
            Collection<TopicListing> topicListings = listTopicsResult.listings().get();
            topicListings.forEach(topicListing -> log.info("{}", topicListing));
        }
    }

}
