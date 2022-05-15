Všeobecné informace k materiálům:

Příklady jsou rozděleny do skupin:

- (A) Základy Kafky: consumer, producer, avro (vše bez Streamů)
- (B) Kafka Streams
- (C) ksqlDB

Číslování u příkladů specifikuje pořadí, v jakém se na školení příklady probírají
  a většinou také obtížnost (1 = nejmenší obtížnost)

U většiny příkladů (kromě příkladu, který má v názvu confluent-cloud) se předpokládá,
  že máte na počítači nainstalovaný Docker.
  
Obyčejně příklad začíná zavoláním:

    docker compose up -d

Poznámka: tento příkaz se spouští v adresáři příkladu tam, kde je soubor docker-compose.yml

A po ukončení příkladu zavolat:

    docker compose down

Poznámka: Používám nový formát Docker Compose v2 (docker compose), je také možné používat starší (pomocí docker-compose), ale doporučuji přechod na v2. Na Windows je v2 automaticky nainstalovaná.