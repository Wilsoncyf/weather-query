package com.wilson.weatherquery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilson.weatherquery.model.UserOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogProducerService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOG_QUEUE_KEY = "sys:log:queue";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将日志推入队列 (RPUSH)
     */
    public void sendLog(UserOperationLog userLog) {
        try {
            // 1. 手动序列化，确保对存储格式的绝对控制
            String jsonLog = objectMapper.writeValueAsString(userLog);
            
            // 2. 推入 Redis List 尾部 (Right Push)
//            redisTemplate.opsForList().rightPush(LOG_QUEUE_KEY, jsonLog);
            redisTemplate.opsForList().leftPush(LOG_QUEUE_KEY, jsonLog);

            log.info("Log produced for user: {}", userLog.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Serialization failed", e);
            // 在实际工程中，这里可能需要 fallback 方案或者报警
        }
    }
}