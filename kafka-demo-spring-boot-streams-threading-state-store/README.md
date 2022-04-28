1. Build:


    mvn clean package

2. spustit Producer:


       java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=producer_app --server.port=8181

3. spustit Streams app:


      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=streams_app --server.port=8182 --spring.kafka.streams.properties.num.stream.threads=1 --spring.kafka.streams.state-dir=target/store_1 --spring.kafka.streams.properties.num.standby.replicas=1 --spring.kafka.streams.properties.application.server=localhost:8182
      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=streams_app --server.port=8183 --spring.kafka.streams.properties.num.stream.threads=1 --spring.kafka.streams.state-dir=target/store_2 --spring.kafka.streams.properties.num.standby.replicas=1 --spring.kafka.streams.properties.application.server=localhost:8183

