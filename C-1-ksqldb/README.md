        # V konzoli:
        docker compose up -d

        # V kontejneru s Kafkou:
        kafka-topics --bootstrap-server localhost:9092 --topic users --replication-factor 1 --partitions 4 --create

        # V konzoli:
        docker compose exec ksqldb-cli ksql http://ksqldb-server:8088

        set 'auto.offset.reset' = 'earliest';
        show topics;

        print users;

        create stream users (rowkey int key, username varchar) with (kafka_topic='users', value_format='json');

        insert into users (username) values ('izzy');
        insert into users (username) values ('mica');
        insert into users (username) values ('jerry');

        # Pozor, chvilku to trva! (asi 10 vterin)
        select * from users emit changes;
        select 'Hello ' + username as greeting from users emit changes;

        show streams;

        drop stream topic2;

        create stream k_users_transformed with (kafka_topic = 'users_transformed', value_format='avro', partitions=4, replicas=1) 
        as select rowkey, ucase(username) as username from k_users emit changes;
