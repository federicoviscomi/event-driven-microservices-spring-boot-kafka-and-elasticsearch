set -x
docker run -it --network=host confluentinc/cp-kafkacat kafkacat -L -b localhost:19092
echo
docker run -it --network=host confluentinc/cp-kafkacat kafkacat -C -b localhost:19092 -t twitter-topic
