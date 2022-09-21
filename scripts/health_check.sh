#!/bin/bash

CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> 포트 번호 변경 시작"
if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "> nginx에 연결된 애플리케이션이 없습니다."
    exit 1
fi

echo "> 'http://127.0.0.1:${TARGET_PORT}' ... 에서 health check 시작"

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
    echo "> #${RETRY_COUNT} trying..."
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)

    echo "> Health Check 결과 : ${RESPONSE_CODE}"

    if [ ${RESPONSE_CODE} -eq 200 ]; then
        echo "> Health Check 성공"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "> Health Check 실패"
        exit 1
    fi
    sleep 10
done