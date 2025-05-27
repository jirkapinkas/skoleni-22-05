Spusteni ve Windows:

set IMAGE=apache/kafka:latest
docker compose up

Python:

https://kafka-python.readthedocs.io/en/master/usage.html#kafkaproducer

pip install git+https://github.com/dpkp/kafka-python.git


cd /opt/kafka/bin

./kafka-topics.sh --bootstrap-server kafka-2:9092 --list

./kafka-topics.sh --bootstrap-server kafka-2:9092 --describe --topic first_topic