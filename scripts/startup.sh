#!/bin/bash

echo "Starting up docker environment"

cd ../scripts/
docker-compose up --build --force-recreate --renew-anon-volumes -d

sleep 40

echo "Docker environment setup successfully"