Spusteni ve Windows:

set IMAGE=apache/kafka:latest
docker compose up

Python:

https://kafka-python.readthedocs.io/en/master/usage.html#kafkaproducer

pip install git+https://github.com/dpkp/kafka-python.git


/opt/kafka/bin $ ./kafka-topics.sh  --list --bootstrap-server kafka-1:19092
