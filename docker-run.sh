#! /bin/bash
docker run --name spotification --rm -p 8080:8080 --env-file .env -d "$(docker images spotification --format '{{.ID}}')"
