#!/bin/bash

REPOSITORY=/home/ec2-user/app/mytamin
CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> 현재 실행 중인 포트 : ${CURRENT_PORT}"

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
    echo "> nginx에 연결된 애플리케이션이 없습니다."
    exit 1
fi

echo "> ${TARGET_PORT}번 포트에서 실행 중인 애플리케이션 종료"

TARGET_PID=$(lsof -ti tcp:${TARGET_PORT})

if [ ! -z ${TARGET_PID} ]; then
  sudo kill -15 ${TARGET_PID}
  sleep 5
fi

echo "> ${TARGET_PORT}번 포트에 새 애플리케이션 배포"

cp $REPOSITORY/*.jar $REPOSITORY/
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
chmod +x $JAR_NAME

nohup java -jar \
       -Dserver.port=${TARGET_PORT} \
       -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-secret.yml \
       $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

echo "> 새로운 버전이 ${TARGET_PORT}번 포트에서 실행 중 ..."
exit 0