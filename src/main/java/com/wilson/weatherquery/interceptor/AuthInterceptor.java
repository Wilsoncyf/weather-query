package com.wilson.weatherquery.interceptor;

import com.wilson.weatherquery.model.UserSession;
import com.wilson.weatherquery.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从 Header 获取 Token
        // 前端约定 Header: "Authorization: Bearer <token>"
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401); // Unauthorized
            return false;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "

        // 2. 验证 Token
        UserSession session = authService.validateAndRenew(token);

        if (session == null) {
            response.setStatus(401);
            return false;
        }

        // 3. 将用户信息放入 Request 作用域，方便 Controller 使用
        request.setAttribute("currentUser", session);

        return true; // 放行
    }
}