#!/usr/bin/env bash

#======================================================================
# 项目启动shell脚本
# bin目录: 启动脚本
# config目录: 配置文件目录
# lib目录: 应用程序
# logs目录: 项目运行日志目录
# nohup后台运行
#
# author: liuzhenhua
# date: 2019-07-21
#======================================================================
SERVER_NAME=yxy
VERSION=1.0-SNAPSHOT

NOW_DATE=`date +%Y%m%d%H%M%S`
BASE_DIR=`cd $(dirname $0)/..; pwd`

JAR_FILE=$BASE_DIR/lib/$SERVER_NAME-$VERSION.jar

DEFAULT_SEARCH_LOCATIONS="classpath:/,classpath:/config/,file:./,file:./config/"
CUSTOM_SEARCH_LOCATIONS=$DEFAULT_SEARCH_LOCATIONS,file:$BASE_DIR/conf/

LOG_PATH=$BASE_DIR/logs/$SERVER_NAME.out
SYSTEM_LOG_PATH=$BASE_DIR/logs/$SERVER_NAME-$NOW_DATE.log

if [ ! -d "../logs" ];then  
  mkdir ../logs  
fi 

#JVM参数
JVM_OPTS="-Dname=$SERVER_NAME --spring.config.location=$CUSTOM_SEARCH_LOCATIONS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Shanghai -Xms512M -Xmx512M -XX:PermSize=256M -XX:MaxPermSize=512M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps -Xloggc:$SYSTEM_LOG_PATH -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"

JAVA='java'
if [ -z `which java` ]; then
    if [ -z $JAVA_HOME ];
        then
            JAVA=$JAVA_HOME/bin/java
        else
            echo 'Cannot find java command and JAVA_HOME.'
    fi
fi

if [ ! -z `java -version 2>&1 | grep 'version' | awk '{print $3}' | egrep '1.[8].\d*'` ]; then
    nohup $JAVA -jar $JAR_FILE $JVM_OPTS $SERVER_NAME>$LOG_PATH 2>&1 &
    echo $SERVER_NAME' has been started successfully.'
else
        echo 'Java version not support, must be 1.8 or 1.8+.'
fi