#!/usr/bin/env bash

#Stop Execution on the first failure
set -e

SCRIPT_ROOT=$(dirname "$0")

cd "$SCRIPT_ROOT/frontend"

ng build

STATIC_FOLDER="$SCRIPT_ROOT/src/main/resources/static"

if [ -d "$STATIC_FOLDER/browser" ]; then
  echo "Moving files from browser directory to Interface..."
  cd "$STATIC_FOLDER" && mv "$STATIC_FOLDER/browser/"* "$STATIC_FOLDER/"

  echo "Cleaning up browser directory..."
  rm -rf "$STATIC_FOLDER/browser"
fi

cd "$SCRIPT_ROOT"

mvn clean package -DskipTests

docker compose build --no-cache app

docker compose up