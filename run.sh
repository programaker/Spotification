#! /bin/bash
docker run --rm -p80:80 `docker images spotification --format "{{.ID}}"`
