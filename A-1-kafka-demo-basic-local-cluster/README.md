Tento příklad je stejný jako příklad A-1-kafka-demo-basic-local, jenom máme k dispozici cluster,
který se skládá ze 3 brokerů. Topic vytvořený pomocí MainCreateTopic bude mít replication factor = 2.

V tomto příkladu se dá hezky vyzkoušet:

- Co se stane, když vypadne jeden broker (data v clusteru zůstanou neporušena)
- Co znamená in-sync replica (když se vypne jeden broker, tak v kafdrop bude vidět, že repliky nejsou in-sync)