#!/bin/bash

CURRENT_PORT=$(cat /home/ec2-user/service_url.inc  | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "> nginx에 연결된 애플리케이션이 없습니다."
    exit 1
fi

echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | tee /home/ec2-user/service_url.inc

sudo service nginx reload

echo "> Nginx reloaded !"