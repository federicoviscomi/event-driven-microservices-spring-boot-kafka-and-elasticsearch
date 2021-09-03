echo
sleep 0.1
curl -X GET http://localhost:9200/ | jq --color-output
echo
sleep 0.1
curl -X GET http://localhost:9200/twitter-index | jq --color-output
echo
sleep 0.1
curl -X PUT -H "Content-Type: application/json" -d @elastic-mapping.json http://localhost:9200/twitter-index | jq --color-output
echo
sleep 0.1
curl -X GET http://localhost:9200/ | jq --color-output
echo
sleep 0.1
curl -X GET http://localhost:9200/twitter-index | jq --color-output
echo
sleep 0.1
curl -X PUT -H "Content-Type: application/json" -d @tweet.json http://localhost:9200/twitter-index/_doc/1 | jq --color-output
echo
sleep 0.1
curl -X GET http://localhost:9200/twitter-index/_search?q=id:1 | jq --color-output
echo
sleep 0.1