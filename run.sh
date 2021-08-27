mvn clean install -DskipTests
mvn clean install -DskipTests spring-boot:run -pl config-server
docker-compose -f common.yml -f kafka_cluster.yml up --build
mvn spring-boot:run -pl twitter-to-kafka-service