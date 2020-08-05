# kafka-example
Ferramentas utilizadas:
```
Java 11
Kafka 2.4.0
```
Comando para iniciar o zookeeper:
```
./bin/zookeeper-server-start.sh config/zookeeper.properties
```

Comandos para iniciar os serviços do kafka:
```
./bin/kafka-server-start.sh config/server1.properties
./bin/kafka-server-start.sh config/server2.properties
./bin/kafka-server-start.sh config/server3.properties
./bin/kafka-server-start.sh config/server4.properties
./bin/kafka-server-start.sh config/server5.properties
```

Comando para observar a situação dos tópicos:
```
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe
```

Comando para observar a situação dos grupos de serviços:
```
./bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe
```

Configurações do kafka para testes está na pasta "config":
```
zookeeper.properties
server1.properties
server2.properties
server3.properties
server4.properties
server5.properties
```

Url de exemplo para testes:
```
http://localhost:8080/new?email=teste@gmail.com&amount=5000&uuid=12345
```