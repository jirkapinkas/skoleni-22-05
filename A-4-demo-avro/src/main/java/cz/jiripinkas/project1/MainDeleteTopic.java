package cz.jiripinkas.project1;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.List;
import java.util.Properties;

public class MainDeleteTopic {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

        try(AdminClient admin = AdminClient.create(config)) {
            admin.deleteTopics(List.of("first_topic"));
        }
    }

}
