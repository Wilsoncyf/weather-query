package com.wilson.weatherquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SocialService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String FOLLOW_KEY_PREFIX = "user:follows:";

    /**
     * 关注用户
     * SADD user:follows:1001 1002
     */
    public void followUser(String userId, String targetUserId) {
        String key = FOLLOW_KEY_PREFIX + userId;
        redisTemplate.opsForSet().add(key, targetUserId);
    }

    /**
     * 取消关注
     * SREM user:follows:1001 1002
     */
    public void unfollowUser(String userId, String targetUserId) {
        String key = FOLLOW_KEY_PREFIX + userId;
        redisTemplate.opsForSet().remove(key, targetUserId);
    }

    /**
     * 获取我关注的人列表
     * SMEMBERS user:follows:1001
     */
    public Set<Object> getFollowList(String userId) {
        String key = FOLLOW_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 🔥 获取共同关注 (交集)
     * SINTER user:follows:1001 user:follows:1002
     * 场景：查看 "我和这个人的共同好友"
     */
    public Set<Object> getCommonFriends(String userId1, String userId2) {
        String key1 = FOLLOW_KEY_PREFIX + userId1;
        String key2 = FOLLOW_KEY_PREFIX + userId2;
        
        // Redis 直接在内存中进行集合交集运算，无需将数据拉回 Java 层处理，性能极高
        return redisTemplate.opsForSet().intersect(key1, key2);
    }
    
    /**
     * 随机推荐一个我关注的人 (用于抽奖或随机展示)
     * SRANDMEMBER
     */
    public Object randomFriend(String userId) {
        String key = FOLLOW_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().randomMember(key);
    }
}