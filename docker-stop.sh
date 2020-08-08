#! /bin/bash
docker stop "$(docker ps --filter "name=spotification" --format '{{.ID}}')"
