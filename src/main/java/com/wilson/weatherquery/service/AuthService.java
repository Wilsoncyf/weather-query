package com.wilson.weatherquery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilson.weatherquery.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "session:token:";
    private static final long EXPIRE_TIME = 30; // 30分钟过期

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 登录成功，生成 Token
    public String login(String userId, String password) {
        // 1. 模拟校验密码 (实际项目中查数据库)
        if (!"123456".equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // 2. 生成 Token (不带横杠的 UUID)
        String token = UUID.randomUUID().toString().replace("-", "");

        // 3. 创建 Session 对象
        UserSession session = new UserSession(userId, "User-" + userId, "USER");

        try {
            // 4. 序列化为 JSON 字符串
            String jsonUser = objectMapper.writeValueAsString(session);

            // 5. 存入 Redis，设置过期时间
            String key = SESSION_PREFIX + token;
            redisTemplate.opsForValue().set(key, jsonUser, EXPIRE_TIME, TimeUnit.MINUTES);

            return token;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Session serialization failed", e);
        }
    }

    // 验证 Token 并自动续期
    public UserSession validateAndRenew(String token) {
        String key = SESSION_PREFIX + token;
        
        // 1. 查询 Redis
        String jsonUser = (String) redisTemplate.opsForValue().get(key);

        if (jsonUser == null) {
            return null; // Token 无效或已过期
        }

        // 2. 自动续期 (核心逻辑：只要用户有操作，就重置倒计时)
        redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.MINUTES);

        try {
            // 3. 反序列化返回
            return objectMapper.readValue(jsonUser, UserSession.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    // 登出
    public void logout(String token) {
        redisTemplate.delete(SESSION_PREFIX + token);
    }
}