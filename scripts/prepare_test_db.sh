#!/bin/bash
containers=$(docker ps --filter "name=parkwhere*" --format "{{.ID}}")
echo "$containers"
if [ ! -z "$containers" ]
then
    echo "Stopping containers:"
    echo $containers | xargs docker stop
    echo "------ STOPPED ------"
    echo "Removing containers:"
    echo $containers | xargs docker rm
fi

printf "\nDocker Network:\n"
docker network ls

printf "\nStarting parkwhere...\n"

docker-compose up -d

echo "Waiting for postgres container to be up"
timeout 90s bash -c 'until docker exec parkwhere-app_postgresql pg_isready ; do sleep 1 ; done'
postgres_port=$(docker port parkwhere-app_postgresql)
echo "postgres container running : $postgres_port"

printf "\nDocker Containers:\n"
docker ps
printf "\n"

containers=$(docker ps --filter "name=parkwhere*" --format "{{.ID}}")
if [ ! -z "$containers" ]
then
  echo "✅ Packwhere App started successfully!"
else
  echo "Failed to start"
  exit 1
fi