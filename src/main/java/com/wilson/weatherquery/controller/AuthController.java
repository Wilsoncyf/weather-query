package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.model.UserSession;
import com.wilson.weatherquery.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password) {
        return authService.login(userId, password);
    }

    // 需要登录才能访问的测试接口
    @GetMapping("/me")
    public UserSession getMyInfo(HttpServletRequest request) {
        // 从拦截器放入的 Attribute 中取出
        return (UserSession) request.getAttribute("currentUser");
    }
    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
         String authHeader = request.getHeader("Authorization");
         if (authHeader != null && authHeader.startsWith("Bearer ")) {
             String token = authHeader.substring(7);
             authService.logout(token);
         }
         return "Logged out";
    }
}