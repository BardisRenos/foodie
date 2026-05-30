#!/bin/bash

echo "==============================="
echo "   Foodie - Select Profile"
echo "==============================="
echo "1) dev"
echo "2) uat"
echo "3) prod"
echo "==============================="
read -p "Enter choice [1-3]: " choice

case $choice in
  1)
    echo "Starting DEV profile..."
    docker compose --profile dev up --build
    ;;
  2)
    echo "Starting UAT profile..."
    docker compose --env-file .env.uat --profile uat up --build
    ;;
  3)
    echo ">> WARNING: You are starting PRODUCTION <<"
    read -p "Are you sure? (yes/no): " confirm
    if [ "$confirm" == "yes" ]; then
      echo "Starting PROD profile..."
      docker compose --env-file .env.prod --profile prod up --build
    else
      echo "Aborted."
      exit 0
    fi
    ;;
  *)
    echo "Invalid choice. Exiting."
    exit 1
    ;;
esac