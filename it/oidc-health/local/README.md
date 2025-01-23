

# Manual tests


```shell
docker compose start postgres keycloak-private
```

```shell
export TKPR=$(curl -X POST "http://localhost:8083/realms/onecx/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "username=onecx" -d "password=onecx"  -d 'grant_type=password' -d 'client_id=onecx-shell-ui-client' -d 'client_secret=secret' | jq -r .access_token)
```

```shell
curl -v -X GET -H "Authorization: Bearer $TKPR" -H "ContentType: application/json" http://localhost:8080/test1
```

```shell
docker compose start keycloak-public
```

```shell
export TKPU=$(curl -X POST "http://localhost:8084/realms/onecx/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "username=onecx" -d "password=onecx"  -d 'grant_type=password' -d 'client_id=onecx-shell-ui-client' -d 'client_secret=secret' | jq -r .access_token)
```

```shell
curl -v -X GET -H "Authorization: Bearer $TKPU" -H "ContentType: application/json" http://localhost:8080/test1
```