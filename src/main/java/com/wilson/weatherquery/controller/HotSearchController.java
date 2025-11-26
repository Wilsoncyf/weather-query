package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.HourlyRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class HotSearchController {

    @Autowired
    private HourlyRankService hourlyRankService;

    // curl -X POST "http://localhost:8080/search?keyword=redis"
    // curl -X POST "http://localhost:8080/search?keyword=java"
    // curl -X GET "http://localhost:8080/hot/24h"
    //Score updated for u1001%

    @PostMapping("/search")
    public String search(@RequestParam String keyword) {
        hourlyRankService.addSearch(keyword);
        return "Searched: " + keyword;
    }

    @GetMapping("/hot/24h")
    public Set<String> getHot24h() {
        // 获取最近 24 小时的热搜
        Set<ZSetOperations.TypedTuple<Object>> results = hourlyRankService.getHotSearch(24);
        
        // 格式化输出
        return results.stream()
                .map(t -> t.getValue() + " (" + t.getScore().intValue() + ")")
                .collect(Collectors.toSet());
    }
}