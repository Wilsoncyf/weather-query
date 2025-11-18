package com.wilson.weatherquery.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public void initBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("user_bloom_filter:");
        bloomFilter.tryInit(1000000, 0.03);  // 初始化布隆过滤器，100万数据，错误率为 3%
        
        // 假设从数据库中加载所有合法的 userId
        bloomFilter.add("userId1");
        bloomFilter.add("userId2");
        // ...
    }
}
