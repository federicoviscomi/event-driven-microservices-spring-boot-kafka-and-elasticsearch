docker-compose -f common.yml -f kafka_cluster.yml down
sleep 1
docker-compose -f common.yml -f kafka_cluster.yml down
sleep 1
docker-compose -f common.yml -f kafka_cluster.yml up --build
