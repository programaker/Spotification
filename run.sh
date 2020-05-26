#! /bin/bash
docker run --name spotification --rm -p80:80 --env-file .env "$(docker images spotification --format '{{.ID}}')"
