1. Build:


      mvn clean package

1. Spustit Producer:


      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=producer_app --server.port=8181

2. Spustit Streams app:


      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=streams_app --server.port=8182 --spring.kafka.streams.properties.num.stream.threads=1
      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=streams_app --server.port=8183 --spring.kafka.streams.properties.num.stream.threads=1

NEBO:

      java -jar target/demo-streams-spring-0.0.1-SNAPSHOT.jar --spring.profiles.active=streams_app --server.port=8182 --spring.kafka.streams.properties.num.stream.threads=2

V topologii jsou 2 sub-topologie a kazda se zprocesovava ve svem vlakne