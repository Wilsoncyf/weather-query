package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/social")
public class SocialController {

    @Autowired
    private SocialService socialService;

    @PostMapping("/follow")
    public String follow(@RequestParam String userId, @RequestParam String targetId) {
        socialService.followUser(userId, targetId);
        return "Followed successfully";
    }

    @GetMapping("/common")
    public Set<Object> getCommon(@RequestParam String userA, @RequestParam String userB) {
        return socialService.getCommonFriends(userA, userB);
    }
    
    @GetMapping("/list")
    public Set<Object> list(@RequestParam String userId) {
        return socialService.getFollowList(userId);
    }
}