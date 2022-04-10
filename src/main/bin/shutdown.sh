#!/usr/bin/env bash
#/bin/bash

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

PID=`pgrep -f yxy`

if [ -z "${PID}" ];
then
        echo 'Cannot find service process.'
else
        kill -9 $PID
        echo 'service has been shutdown successfully.'
fi