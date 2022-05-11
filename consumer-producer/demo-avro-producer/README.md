1. Spustit Kafku:


      docker compose up -d

2. Provest build aplikace (vygeneruje do `target/generates-sources` tridy na zaklade `src/main/avro/*.avsc` souboru)


      mvn clean package

3. Spustit MainProducer a MainConsumer
4. Timto se prida schema (na Linuxu). Pokud se neprida rucne, pak ho Producer automaticky vytvori (tohle chovani Kafky se da vypnout):

curl -X POST \
'http://localhost:8081/subjects/first_topic-value/versions' \
--header 'Content-Type: application/vnd.schemaregistry.v1+json' \
--data-raw '{"schema" : "{\"type\":\"record\",\"name\":\"Movie\",\"namespace\":\"cz.jiripinkas.project1\",\"fields\":[{\"name\":\"title\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"year\",\"type\":\"int\"}]}"}'


6. Je mozne menit schema pomoci non-breaking changes napriklad pridanim tohoto fieldu:


    {
        "name" : "stuff",
        "type" : ["null", "string"],
        "default" : null
    }

6. Schema lze krasne videt v AKHQ: http://localhost:8080/ui/docker-kafka-server/schema
7. NEBO primo ve schema registry: http://localhost:8081/schemas
