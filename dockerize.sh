#!/usr/bin/bash

mvn clean package -DskipTests

docker compose build --no-cache app

docker compose up