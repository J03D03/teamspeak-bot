#!/bin/bash

# pull the bot from dockerhub
docker pull j03d03/teamspeak-channel-bot:latest
# stop and remove container
docker stop ChannelBot
docker rm ChannelBot
# start container
docker run --name ChannelBot --restart always -d --env-file ./env.list --network="host" j03d03/teamspeak-channel-bot:latest
