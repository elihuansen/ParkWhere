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
    echo "------ REMOVED ------"
fi
echo "Starting parkwhere..."
docker-compose up -d

echo "Waiting for postgresql to be up"
timeout 90s bash -c 'until docker exec parkwhere-app_postgresql_1 pg_isready ; do sleep 1 ; done'

containers=$(docker ps --filter "name=parkwhere*" --format "{{.ID}}")
if [ ! -z "$containers" ]
then
  echo "✅ Packwhere App started successfully!"
else
  echo "Failed to start"
  exit 1
fi