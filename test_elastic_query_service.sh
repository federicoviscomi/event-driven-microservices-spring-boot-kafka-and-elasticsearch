curl --location --request GET 'http://localhost:8183/elastic-query-service/documents/' \
--header 'Content-Type: application/json' \
--header 'token: dev' \
--header 'Authorization: Basic dGVzdDp0ZXN0MTIzNA==' \
--header 'Cookie: JSESSIONID=24B0D8FBA6E171BA80373B82AA0717F9' \
--data-raw '{
    "text": "test"
}' | jq --color-output



curl --location --request GET 'http://localhost:8183/elastic-query-service/documents/6454734222187089566' \
--header 'Content-Type: application/json' \
--header 'token: dev' \
--header 'Authorization: Basic dGVzdDp0ZXN0MTIzNA==' \
--header 'Cookie: JSESSIONID=24B0D8FBA6E171BA80373B82AA0717F9' \
--data-raw '{
    "text": "test"
}' | jq --color-output


curl --location --request POST 'http://localhost:8183/elastic-query-service/documents/get-document-by-text' \
--header 'Content-Type: application/json' \
--header 'token: dev' \
--header 'Authorization: Basic dGVzdDp0ZXN0MTIzNA==' \
--header 'Cookie: JSESSIONID=BCE54A6BA884C3628FBA732044D5E67A' \
--data-raw '{
    "text": "Java"
}' | jq --color-output