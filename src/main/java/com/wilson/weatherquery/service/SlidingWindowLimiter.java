package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlidingWindowLimiter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private static final String SLIDING_WINDOW_KEY = "rate-limiter:sliding-window";
    private static final int MAX_REQUESTS = 5; // 每秒最大请求数
    private static final int WINDOW_SIZE = 60; // 时间窗口大小，单位：秒

    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis() / 1000; // 当前时间（秒）

        // 移除超过窗口大小的请求
        redisTemplate.opsForZSet().removeRangeByScore(SLIDING_WINDOW_KEY, 0, currentTime - WINDOW_SIZE);

        // 获取当前时间窗口内的请求数量
        Long requestCount = redisTemplate.opsForZSet().count(SLIDING_WINDOW_KEY, currentTime - WINDOW_SIZE, currentTime);

        if (requestCount != null && requestCount < MAX_REQUESTS) {
            // 如果请求数未达到最大限制，允许请求
            redisTemplate.opsForZSet().add(SLIDING_WINDOW_KEY, String.valueOf(currentTime), currentTime);
            return true; // 请求通过
        } else {
            // 超过最大请求数，拒绝请求
            return false; // 请求被拒绝
        }
    }
}
