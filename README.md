# kafka-example
Comando para iniciar o zookeeper
```
./bin/zookeeper-server-start.sh config/zookeeper.properties
```

Comando para iniciar o kafka
```
./bin/kafka-server-start.sh config/server.properties
```

Comando para observar a situação dos tópicos
```
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe
```

Comando para observar a situação dos grupos de serviços
```
./bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe
```
