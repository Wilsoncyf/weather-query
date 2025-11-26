package com.wilson.weatherquery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilson.weatherquery.model.UserOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class LogConsumerWorker {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOG_QUEUE_KEY = "sys:log:queue";
    // 新增：备份队列（处理中队列）
    private static final String PROCESSING_QUEUE_KEY = "sys:log:processing";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void saveLogToDb(UserOperationLog logData) {
        // 模拟耗时操作
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        log.info("✅ [DB Saved] User: {}, Operation: {}", logData.getUserId(), logData.getOperation());
    }

    @Bean
    public ApplicationRunner runConsumer() {
        return args -> {
            new Thread(() -> {
                log.info("🛡️ Reliable Log Consumer Started...");
                while (true) {
                    try {
                        // 1. 可靠获取：从 Queue 右边弹出，放入 Processing 左边
                        // 相当于 Redis 命令：BRPOPLPUSH sys:log:queue sys:log:processing 5
                        Object data = redisTemplate.opsForList()
                                .rightPopAndLeftPush(LOG_QUEUE_KEY, PROCESSING_QUEUE_KEY, 5, TimeUnit.SECONDS);

                        if (data != null) {
                            String jsonLog = (String) data;
                            UserOperationLog userLog = objectMapper.readValue(jsonLog, UserOperationLog.class);

                            // 2. 执行业务逻辑
                            saveLogToDb(userLog);

                            // 3. ACK 确认：业务成功后，从"处理中队列"删除该记录
                            // LREM sys:log:processing 1 {jsonLog}
                            redisTemplate.opsForList().remove(PROCESSING_QUEUE_KEY, 1, jsonLog);
                            // log.info("🗑️ Removed from processing queue");
                        }
                    } catch (Exception e) {
                        log.error("❌ Error consuming log", e);
                        // 注意：这里如果报错，数据依然保留在 PROCESSING_QUEUE_KEY 中，等待人工介入或补偿脚本处理
                        try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                    }
                }
            }).start();
        };
    }
}