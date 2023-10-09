# Copia oggetto su as400
    CPY OBJ('/qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/kafkaas400.jar') 
        TOOBJ('/tmp/kafkaas400.jar')   REPLACE(*YES)
    qsh
    java -jar /tmp/kafkaas400.jar
    TELNET RMTSYS('fcvlkac4.fc.crtnet') PORT(9092)    
    TELNET RMTSYS('fcvlkas1.fc.crtnet') PORT(9092)    
# Istruzioni per usare kafka con docker compose
    https://www.baeldung.com/ops/kafka-docker-setup
# Far partire kafka con il comando:
    docker-compose up -d
    Eseguire un cimando nel docker
        docker exec -it kafka-kafka-1 /bin/sh  
        cd /usr/bin  
    List dei topics
        kafka-topics --list --bootstrap-server localhost:9092
    Creazione topic
        kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic sampleTopic
    Produrre dei messaggi
        kafka-console-producer --broker-list localhost:9092 --topic sampleTopic
    Consumare dei messaggi
        kafka-console-consumer --bootstrap-server localhost:9092 --topic sampleTopic --from-beginning

# Comandi kafka cli (https://www.conduktor.io/kafka/kafka-producer-cli-tutorial/)
    - cd /usr/bin
    - ./kafka-topics  --bootstrap-server localhost:9092 --list
    - ./kafka-topics  --bootstrap-server localhost:9092 --topic uno --create
    - ./kafka-topics  --bootstrap-server localhost:9092 --topic uno --delete
    - ./kafka-topics  --bootstrap-server localhost:9092 --topic uno --describe
    - ./kafka-console-producer --bootstrap-server localhost:9092 --topic uno
    - ./kafka-console-producer --bootstrap-server localhost:9092 --topic uno < /tmp/topic_input.txt
    - ./kafka-console-consumer --bootstrap-server localhost:9092 --topic uno --from-beginning

# ############################################################################################################################################
# Ubuntu: installazione dell'immagine docker linux e di kafka
    Per eseguire il docker container (la prima volta):
        docker run -it -p 9092:9092 -v C:\Users\fce458\Downloads:/dwn --name=gf-ubuntu ubuntu /bin/bash 
    Par eseguire l'attach al docker
        docker start gf-ubuntu
        docker attach gf-ubuntu
    Installazione componenti di linux e  comandi utili:
        alias ll='ls -l'
        apt-get update
        apt-get install vim net-tools curl iputils* curl telnet default-jre wget 
    Installare kafka:
        cd /tmp
        wget https://downloads.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
        tar xvf kafka_2.13-3.5.1.tgz
        mv kafka_2.13-3.5.1 /usr/local/kafka
        cd /usr/local/kafka
    Far partire zookeper in batch con il file di configurazione presente in config
        nohup bin/zookeeper-server-start.sh config/zookeeper.properties &> zookeeper.out &
    Far partire kafka in batch con il file di configurazione presente in config        
        nohup bin/kafka-server-start.sh config/server.properties &> kafka.out &
    Creazione topic
        bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic sampleTopic
    List dei topics
        bin/kafka-topics.sh --list --bootstrap-server localhost:9092
    Produrre dei messaggi
        bin/kafka-console-producer.sh --broker-list localhost:9092 --topic sampleTopic
    Consumare dei messaggi
        bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sampleTopic --from-beginning

# Ubuntu: operazioni da eseguire alla partenza della immagine linux
    Par eseguire lo start e l'attach al docker
        docker start gf-ubuntu
        docker attach gf-ubuntu
    Far partire zookeper in batch con il file di configurazione presente in config
        nohup bin/zookeeper-server-start.sh config/zookeeper.properties &> zookeeper.out &
    Far partire kafka in batch con il file di configurazione presente in config        
        nohup bin/kafka-server-start.sh config/server.properties &> kafka.out &
    List dei topics
        bin/kafka-topics.sh --list --bootstrap-server localhost:9092
    Consumare dei messaggi
        bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sampleTopic --from-beginning

# Sviluppo dell'applicazione 
    https://developer.okta.com/blog/2019/11/19/java-kafka