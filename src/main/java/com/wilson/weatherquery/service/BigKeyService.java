package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Service
public class BigKeyService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BIG_KEY = "test:bigkey:5mb";

    // 1. 制造 Big Key (写入一个 5MB 的字符串)
    public String createBigKey() {
        // 创建一个 5MB 的 byte 数组
        char[] chars = new char[5 * 1024 * 1024 * 10];
        Arrays.fill(chars, 'A');
        String bigValue = new String(chars);

        redisTemplate.opsForValue().set(BIG_KEY, bigValue);
        return "Big Key Created (5MB)";
    }

    // 2. 模拟读取 (计时)
    public String readBigKey() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 这一步会非常慢，且消耗大量网络带宽
        String value = (String) redisTemplate.opsForValue().get(BIG_KEY);

        stopWatch.stop();
        return "Read 5MB Key took: " + stopWatch.getTotalTimeMillis() + " ms";
    }

    // 3. 优雅删除 (UNLINK)
    public String deleteBigKey() {
        // DEL 会阻塞主线程
        // UNLINK (Redis 4.0+) 会在后台线程异步释放内存
        Boolean result = redisTemplate.unlink(BIG_KEY);
        return "Unlinked: " + result;
    }
}