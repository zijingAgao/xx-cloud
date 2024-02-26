#!/bin/bash

#开始时间 时间戳
startTime=`date +'%Y-%m-%d %H:%M:%S'`

#接口项目站点路径（目录按照各自配置）
APP_PATH=/home/xx-system

#jar包文件名称
APP_NAME=$APP_PATH/admin-1.0.0.jar

echo "开始停止 xx-system 项目进程"
#查询进程，并杀掉当前jar/java程序

pid=`ps -ef|grep $APP_NAME | grep -v grep | awk '{print $2}'`
if [ $pid ];then
  echo "pid: $pid"
  kill -9 $pid
  echo "xx-system 项目进程进程终止成功"
fi

