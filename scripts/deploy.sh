#!/bin/bash

REPOSITORY=/home/ec2-user/app/mytamin
PROJECT_NAME=MyTamin_BE

echo "> 새 애플리케이션 배포"

# Build 파일 복사
cp $REPOSITORY/zip/*.jar $REPOSITORY/

# 현재 구동중인 애플리케이션 pid 확인 및 종료
CURRENT_PID=$(lsof -ti tcp:8080)

if [ -z "$CURRENT_PID" ]; then
   echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
   echo "> kill -15 $CURRENT_PID"
   kill -15 $CURRENT_PID
   sleep 5
fi

# $JAR_NAME 에 실행권한 추가
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
chmod +x $JAR_NAME
echo "> JAR Name: $JAR_NAME"

nohup java -jar \
       -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-secret.yml \
       $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &