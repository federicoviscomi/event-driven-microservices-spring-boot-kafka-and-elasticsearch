set -x
set -euo pipefail
sleep 0.5
cd docker-compose || exit
sleep 0.5
docker-compose down
sleep 0.5
cd ..
sleep 0.5
mvn clean install -DskipTests -X -U
sleep 0.5
#mvn spring-boot:run -pl twitter-to-kafka-service
cd docker-compose || exit
sleep 0.5
docker-compose down
sleep 0.5
docker-compose up --build || true
sleep 0.5
docker-compose down
sleep 0.5
