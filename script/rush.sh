#!/bin/bash
# 模拟 20 个并发请求
for i in {1..20}
do
   # & 符号让请求在后台并发执行
   curl "http://localhost:8080/coupon/rush?userId=user$i&safe=true" &
done