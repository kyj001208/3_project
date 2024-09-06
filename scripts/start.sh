#!/bin/bash

ROOT_DIR="/home/ec2-user/codedeploy"
JAR_FILE="$ROOT_DIR/build/libs/*.jar"
DATE=`date +"[%Y-%m-%d %H:%M:%S]"`
DEPLOY_LOG="$ROOT_DIR/deploy.log"

#jar 파일실행
# 배포Error발생 scripts/start.sh failed to close STDOUT ( > $ROOT_DIR/nohup.out 2>&1 & ) 표준출력,표준에러 nohup.out으로 리다이렉트 된다
nohup java -jar $JAR_FILE > $ROOT_DIR/nohup.out 2>&1 &
#echo "$JAR_FILE 실행완료!"
echo "##################################" >> $DEPLOY_LOG
echo "$DATE : jar 파일을 실행하였습니다." >> $DEPLOY_LOG
