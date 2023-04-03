#!/bin/bash

echo [docker compose up] stop Main App docker container

(exec docker-compose stop infra-home)

echo [Main App FINISHED] container has been stopped