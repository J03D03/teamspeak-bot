#!/bin/bash

# pull the bot from dockerhub
docker pull j03d03/teamspeak-legacy-bot:latest
# stop and remove container
docker stop LegacyBot
docker rm LegacyBot
# start container
docker run --name LegacyBot --restart always -d --env-file ./env.list --network="host" j03d03/teamspeak-legacy-bot:latest
