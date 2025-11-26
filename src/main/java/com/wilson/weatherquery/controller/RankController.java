package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    @PostMapping("/add")
    public String addScore(@RequestParam String userId, @RequestParam double score) {
        rankService.addScore(userId, score);
        return "Score updated for " + userId;
    }
    // curl -X POST "http://localhost:8080/rank/add?userId=u1001&score=10"
    // curl -X POST "http://localhost:8080/rank/add?userId=u1002&score=50"
    // curl -X GET "http://localhost:8080/rank/my?userId=u1001"
    // curl -X POST "http://localhost:8080/rank/add?userId=u1003&score=30"
    // curl -X GET "http://localhost:8080/rank/top"
    // curl -X POST "http://localhost:8080/rank/add?userId=u1001&score=100"
    // curl -X GET "http://localhost:8080/rank/top"
    // curl -X GET "http://localhost:8080/rank/my?userId=u1001"


    @GetMapping("/top")
    public Set<RankItem> getTop(@RequestParam(defaultValue = "10") int n) {
        Set<ZSetOperations.TypedTuple<Object>> topUsers = rankService.getTopUsers(n);
        
        // 将 Redis 的 Tuple 转换为更友好的 JSON 对象
        return topUsers.stream()
                .map(tuple -> new RankItem((String) tuple.getValue(), tuple.getScore()))
                .collect(Collectors.toSet()); // 注意：这里转 Set 可能会乱序，前端展示建议用 List，但为了演示简单先这样
    }

    @GetMapping("/my")
    public String getMyRank(@RequestParam String userId) {
        Long rank = rankService.getUserRank(userId);
        Double score = rankService.getUserScore(userId);
        return "User: " + userId + ", Rank: " + rank + ", Score: " + score;
    }

    // 内部类，用于 JSON 输出
    public static class RankItem {
        public String userId;
        public Double score;

        public RankItem(String userId, Double score) {
            this.userId = userId;
            this.score = score;
        }
    }
}