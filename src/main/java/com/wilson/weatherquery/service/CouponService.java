package com.wilson.weatherquery.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CouponService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final String COUPON_STOCK_KEY = "activity:coupon:stock";
    private static final String LOCK_KEY = "lock:coupon:seckill";

    // 初始化库存 (测试用)
    public void initStock() {
        redisTemplate.opsForValue().set(COUPON_STOCK_KEY, "5"); // 只有5张
    }

    /**
     * 错误示范：不加锁，或者只用简单的 Redis 锁
     */
    public String rushCouponUnsafe(String userId) {
        // 修改前: (Integer) redisTemplate... -> 报错
        // 修改后: 先转 String 再 parseInt
        String stockStr = (String) redisTemplate.opsForValue().get(COUPON_STOCK_KEY);
        int stock = stockStr == null ? 0 : Integer.parseInt(stockStr);

        if (stock > 0) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}

            redisTemplate.opsForValue().decrement(COUPON_STOCK_KEY);
            log.info("用户 {} 抢到了！剩余: {}", userId, stock - 1);
            return "Success";
        }
        return "Failed";
    }

    /**
     * ✅ 正确示范：Redisson 分布式锁
     */
    public String rushCouponSafe(String userId) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            boolean isLocked = lock.tryLock(5, -1, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    // --- 修改读取逻辑 ---
                    String stockStr = (String) redisTemplate.opsForValue().get(COUPON_STOCK_KEY);
                    int stock = stockStr == null ? 0 : Integer.parseInt(stockStr);
                    // ------------------

                    if (stock > 0) {
                        redisTemplate.opsForValue().decrement(COUPON_STOCK_KEY);
                        log.info("🎉 用户 {} 抢到了！剩余: {}", userId, stock - 1);
                        return "Success";
                    } else {
                        log.warn("😭 用户 {} 来晚了，没库存了", userId);
                        return "No Stock";
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                return "System Busy";
            }
        } catch (InterruptedException e) {
            return "Error";
        }
//        return "Failed";
    }
}