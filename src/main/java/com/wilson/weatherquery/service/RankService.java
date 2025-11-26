package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RankService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LEADERBOARD_KEY = "rank:user:activity";

    /**
     * 增加用户积分
     * ZINCRBY rank:user:activity 1.0 u1001
     */
    public void addScore(String userId, double score) {
        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, userId, score);
    }

    /**
     * 获取 Top N 用户 (从高到低)
     * ZREVRANGE rank:user:activity 0 N-1
     */
    public Set<ZSetOperations.TypedTuple<Object>> getTopUsers(int n) {
        // reverseRangeWithScores 返回包含分数的结果
        return redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);
    }

    /**
     * 获取用户的具体排名 (从 1 开始)
     * ZREVRANK rank:user:activity u1001
     */
    public Long getUserRank(String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, userId);
        // Redis 排名是从 0 开始的，所以要 +1
        return (rank != null) ? rank + 1 : -1;
    }

    /**
     * 获取用户当前分数
     * ZSCORE rank:user:activity u1001
     */
    public Double getUserScore(String userId) {
        return redisTemplate.opsForZSet().score(LEADERBOARD_KEY, userId);
    }
}