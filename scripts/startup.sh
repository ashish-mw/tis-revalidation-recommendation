#!/bin/bash

echo "Starting up docker environment"

cd ../scripts/
docker-compose up -d

sleep 40

echo "Docker environment setup successfully"