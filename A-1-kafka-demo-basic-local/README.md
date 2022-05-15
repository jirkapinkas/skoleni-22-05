Zavolat:

    docker-compose up -d

Spustit MainCreateTopic (vytvoří topic first_topic s pěti partitions)

Spustit MainProducer, tím se budou do topicu ukládat testovací data

Spustit aplikace MainConsumer1, 2 a 3 a na tom je vidět, jak Kafka distribuuje zátěž mezi více consumerů

Topic je možné smazat pomocí MainDeleteTopic

List topicu:

    docker run --rm --tty --network kafka_net confluentinc/cp-kafkacat kafkacat -b kafka:9092 -L

Obsah topicu first_topic (kafkacat bude fungovat jako consumer):

    docker run --rm --tty --network kafka_net confluentinc/cp-kafkacat kafkacat -b kafka:9092 -t first_topic

Kafdrop:

    http://localhost:9000