Zavolat:

    docker-compose up -d

Spustit:

    Aplikaci producera a consumera

List topicu:

    docker run --rm --tty --network kafka_net confluentinc/cp-kafkacat kafkacat -b kafka_1:9092 -L

Obsah topicu first_topic (kafkacat bude fungovat jako consumer):

    docker run --rm --tty --network kafka_net confluentinc/cp-kafkacat kafkacat -b kafka_1:9092 -t first_topic

Kafdrop:

    http://localhost:9000