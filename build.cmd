@echo off
color 0A
docker build -t registry.ezisolutions.tech/eziops/app-gw:v1 .
docker push registry.ezisolutions.tech/eziops/app-gw:v1
