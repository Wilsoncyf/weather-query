package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class HourlyRankService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "rank:hourly:";

    // 1. 增加热度 (写入当前小时的桶)
    public void addSearch(String keyword) {
        // 生成 Key: rank:hourly:2025112115 (年月日时)
        String currentKey = PREFIX + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        
        // ZINCRBY
        redisTemplate.opsForZSet().incrementScore(currentKey, keyword, 1);
        
        // 设置过期时间 (25小时后自动删除，留1小时冗余)
        redisTemplate.expire(currentKey, 25, TimeUnit.HOURS);
    }

    // 2. 获取最近 N 小时的热搜榜 (聚合计算)
    public Set<ZSetOperations.TypedTuple<Object>> getHotSearch(int hours) {
        String destKey = "rank:temp:aggregated";
        List<String> keysToUnion = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");

        // 生成最近 N 小时的所有 Key
        for (int i = 0; i < hours; i++) {
            keysToUnion.add(PREFIX + now.minusHours(i).format(formatter));
        }

        // ZUNIONSTORE: 计算并集，将结果存入 destKey
        // 这里的 sum 意味着如果有相同的 keyword，分数相加
        redisTemplate.opsForZSet().unionAndStore(keysToUnion.get(0), keysToUnion.subList(1, keysToUnion.size()), destKey);
        
        // 设置临时 Key 过期时间 (1分钟即可，查完就丢)
        redisTemplate.expire(destKey, 60, TimeUnit.SECONDS);

        // 取出 Top 10
        return redisTemplate.opsForZSet().reverseRangeWithScores(destKey, 0, 9);
    }
}