#!/usr/bin/env bash
cd /opt/jboss/keycloak/bin

# Downlaod jq as we don't want to parse JSON by hand even if it's only a user ID
curl -L https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 --output jq
chmod +x jq

./kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin --password password

USERID=$(./kcadm.sh create users -r TestRealm -s username=admin -s enabled=true -o --fields id | ./jq '.id' | tr -d '"')
echo $USERID
./kcadm.sh update users/$USERID/reset-password -r TestRealm -s type=password -s value=admin -s temporary=false -n
./kcadm.sh add-roles --uusername admin --rolename basic-token -r TestRealm
