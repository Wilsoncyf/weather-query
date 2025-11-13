package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketLimiter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_BUCKET_KEY = "rate-limiter:token-bucket";
    private static final int MAX_TOKENS = 5;  // 每秒最大请求数
    private static final int REFILL_RATE = 2;  // 每秒生成的令牌数量

    // 尝试获取令牌
    public boolean tryAcquire() {
        Long tokenCount = redisTemplate.opsForList().size(TOKEN_BUCKET_KEY);
        System.out.println("当前令牌数量：" + tokenCount + ", 最大令牌数量：" + MAX_TOKENS);  // 添加日志

        if (tokenCount != null && tokenCount > 0) {
            // 如果令牌桶中有令牌，消费一个令牌
            redisTemplate.opsForList().leftPop(TOKEN_BUCKET_KEY);
            return true;  // 请求通过
        } else {
            return false;  // 请求被拒绝
        }
    }



    // 定时任务：每秒刷新令牌，生成 `REFILL_RATE` 个令牌
    @Scheduled(fixedRate = 1000)
    public void refill() {
        Long tokenCount = redisTemplate.opsForList().size(TOKEN_BUCKET_KEY);
        System.out.println("当前令牌数量：" + tokenCount + ", 最大令牌数量：" + MAX_TOKENS);  // 添加日志

        // 如果令牌桶中的令牌数量小于最大容量，则继续添加令牌
        if (tokenCount != null && tokenCount < MAX_TOKENS) {
            for (int i = 0; i < REFILL_RATE; i++) {
                redisTemplate.opsForList().leftPush(TOKEN_BUCKET_KEY, "token");
            }
            System.out.println("Refilled token.");  // 日志输出
        } else {
            System.out.println("令牌桶已满，未添加新的令牌");  // 日志输出
        }
    }


}
