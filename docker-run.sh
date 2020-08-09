#! /bin/bash

docker run --rm -d \
-p 8080:8080 \
--name spotification \
--env-file .env \
"$(docker images spotification --format '{{.ID}}' | head -n 1)"
