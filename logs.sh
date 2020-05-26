#! /bin/bash
docker logs -f "$(docker ps --filter "name=spotification" --format '{{.ID}}')"
