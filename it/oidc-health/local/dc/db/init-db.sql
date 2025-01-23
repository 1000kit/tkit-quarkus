-- --keycloak
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'keycloak';
CREATE DATABASE keycloak with owner keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;

-- --keycloak-public
CREATE USER keycloak_public WITH ENCRYPTED PASSWORD 'keycloak_public';
CREATE DATABASE keycloak_public with owner keycloak_public;
GRANT ALL PRIVILEGES ON DATABASE keycloak_public TO keycloak_public;
