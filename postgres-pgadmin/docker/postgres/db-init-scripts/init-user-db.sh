set -x
set -e

#psql -d keycloak -U keycloak
#psql --command "CREATE USER keycloak WITH PASSWORD 'keycloak';"
#psql --command "CREATE DATABASE keycloak;"
#psql --command "GRANT ALL PRIVILEGES ON DATABASE keycloak to keycloak;"
#psql --command "\q;"

#    CREATE USER keycloak;
#    CREATE DATABASE keycloak;

psql -v ON_ERROR_STOP=1 --username "keycloak" --dbname "keycloak" <<-EOSQL
    CREATE SCHEMA keycloak;
    GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
EOSQL