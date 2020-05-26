#! /bin/bash
docker run --name spotification --rm -p80:8080 --env-file .env -d "$(docker images spotification --format '{{.ID}}')"
