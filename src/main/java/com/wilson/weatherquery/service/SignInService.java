package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisCallback;

import java.time.LocalDate;

@Service
public class SignInService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 用户签到接口
    public String signIn(String userId) {
        String signInKey = "signIn:" + LocalDate.now();  // 每日签到的位图键
        int bitPosition = Math.abs(userId.hashCode());  // 将用户ID映射到一个唯一的bit位置

        // 设置签到位
        redisTemplate.opsForValue().setBit(signInKey, bitPosition, true);
        return "User " + userId + " signed in today!";
    }

    // 查询用户签到情况
    public String checkSignIn(String userId) {
        String signInKey = "signIn:" + LocalDate.now();
        int bitPosition = Math.abs(userId.hashCode());

        // 获取签到位
        boolean hasSignedIn = redisTemplate.opsForValue().getBit(signInKey, bitPosition);
        return hasSignedIn ? "User " + userId + " has signed in." : "User " + userId + " has not signed in.";
    }

    // 统计当天签到人数
    public long getSignInCount() {
        String signInKey = "signIn:" + LocalDate.now();

        // 使用 RedisCallback 解决歧义并调用 bitCount
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.bitCount(signInKey.getBytes())
        );
    }
}
