package com.wilson.weatherquery.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TokenBucketLimiter tokenBucketLimiter;

    private static final String WEATHER_CACHE_KEY = "weather:";

    // 使用令牌桶限流
    public String getWeather(String cityId) {
        // 先通过令牌桶限流检查请求
        if (!tokenBucketLimiter.tryAcquire()) {
            return "Weather service is busy. Please try again later.";  // 如果没有令牌，拒绝请求
        }

        // 获取分布式锁
        RLock lock = redissonClient.getLock("lock:weather:" + cityId);
        try {
            // 尝试加锁，最多等 5 秒，锁住后执行业务
            if (lock.tryLock()) {
                String cacheKey = WEATHER_CACHE_KEY + cityId;
                String weatherData = (String) redisTemplate.opsForValue().get(cacheKey);

                if (weatherData != null) {
                    // 如果缓存中有数据，则直接返回
                    return weatherData;
                }

                // 模拟从外部 API 获取天气数据
                weatherData = "Sunny 25°C";


                // 将查询结果存入 Redis，随机过期时间
                int expireTime = (int)(Math.random() * 60 + 60);
                redisTemplate.opsForValue().set(cacheKey, weatherData, expireTime);

                return weatherData;
            } else {
                return "Weather service is busy. Please try again later.";
            }
        } finally {
            lock.unlock();
        }
    }
}
