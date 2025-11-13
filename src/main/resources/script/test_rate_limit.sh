#!/bin/bash

# 发送请求的次数
REQUESTS=20

# 目标接口地址
URL="http://localhost:8080/weather?cityId=101280601"

# 模拟发送请求
for ((i=1; i<=REQUESTS; i++))
do
    echo "发送请求 #$i"
    RESPONSE=$(curl -s $URL)

    # 打印响应
    echo "响应: $RESPONSE"

    # 等待 0.2 秒，模拟每个请求间隔
    sleep 0.2
done
