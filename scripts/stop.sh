#!/bin/bash

ROOT_DIR="/home/ec2-user/codedeploy"
DEPLOY_LOG="$ROOT_DIR/deploy.log"
DATE=`date +"[%Y-%m-%d %H:%M:%S]"`
FILE_NAME="greenApp.jar"

#jar파일 PID
JAR_PID=`ps -ef | grep $FILE_NAME| grep -v grep | awk '{print $2}'`
#추출한 PID 개수
JAR_CNT=`ps -ef | grep $FILE_NAME | grep -v grep | wc -l`

if [ $JAR_CNT -gt 0 ] 
then
        #jar파일 kill
        sudo kill -TERM $JAR_PID
        echo "$DATE : jar파일을 종료합니다. (PID : $JAR_PID)" >> $DEPLOY_LOG
else
        #jar파일이 존재하지 않을떄
        echo "$DATE : 실행중인 jar파일이 존재하지 않습니다." >> $DEPLOY_LOG
fi
