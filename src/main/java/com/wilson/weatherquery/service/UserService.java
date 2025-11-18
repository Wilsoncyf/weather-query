package com.wilson.weatherquery.service;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private RedissonClient redissonClient;

    private static final String USER_CACHE_KEY = "user:";

    private static final String USER_BLOOM_FILTER = "user_bloom_filter:";

    // 获取用户信息（从缓存中查询）
    public String getUserById(String userId) {

        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(USER_BLOOM_FILTER);

        // 检查数据是否在布隆过滤器中
        if (!bloomFilter.contains(userId)) {
            return "Data not found";  // 如果不存在，直接返回，避免查询数据库
        }

        String cacheKey = USER_CACHE_KEY + userId;

        // 1. 先尝试获取缓存数据
        String userInfo = (String) redisTemplate.opsForValue().get(cacheKey);

        // 2. 如果缓存中没有数据，则需要查询数据库
        if (userInfo == null) {
            // 3. 使用分布式锁，避免多个请求同时查询数据库
            RLock lock = redissonClient.getLock("lock:user:" + userId);
            try {
                // 4. 尝试获取锁，如果能获得锁，进入数据库查询
                if (lock.tryLock()) {
                    // 5. 再次查询缓存，避免已经有其他线程更新缓存
                    userInfo = (String) redisTemplate.opsForValue().get(cacheKey);
                    if (userInfo == null) {
                        // 模拟从数据库查询
                        userInfo = "User info for userId: " + userId;

                        // 将查询结果存入 Redis 缓存
                        redisTemplate.opsForValue().set(cacheKey, userInfo, 60, TimeUnit.MINUTES);
                    }
                } else {
                    // 如果获取锁失败，等待一段时间后重试（可自定义策略）
                    return "Service is busy, please try again later.";
                }
            } finally {
                lock.unlock();  // 确保释放锁
            }
        }
        return userInfo;
    }
}
