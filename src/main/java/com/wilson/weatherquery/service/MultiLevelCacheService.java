package com.wilson.weatherquery.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MultiLevelCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Cache<String, Object> caffeineCache;

    private static final String CACHE_KEY = "user:";

    public MultiLevelCacheService() {
        // 初始化本地缓存
        this.caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)  // 设置过期时间
                .maximumSize(100)  // 设置缓存的最大容量
                .build();
    }

    // 获取用户信息
    public String getUserById(String userId) {
        String cacheKey = CACHE_KEY + userId;

        // 1. 查询本地缓存
        String userInfo = (String) caffeineCache.getIfPresent(cacheKey);
        if (userInfo != null) {
            return userInfo;
        }

        // 2. 查询 Redis 缓存
        userInfo = (String) redisTemplate.opsForValue().get(cacheKey);
        if (userInfo != null) {
            // 如果 Redis 命中，将数据加载到本地缓存
            caffeineCache.put(cacheKey, userInfo);
            return userInfo;
        }

        // 3. 查询数据库（模拟）
        userInfo = queryUserFromDatabase(userId);

        // 4. 回填缓存
        if (userInfo != null) {
            // 将数据存入 Redis 和本地缓存
            redisTemplate.opsForValue().set(cacheKey, userInfo, 60, TimeUnit.MINUTES);
            caffeineCache.put(cacheKey, userInfo);
        }

        return userInfo;
    }

    // 模拟从数据库查询
    private String queryUserFromDatabase(String userId) {
        return "User info for userId: " + userId;  // 假设数据库查询成功
    }
}
