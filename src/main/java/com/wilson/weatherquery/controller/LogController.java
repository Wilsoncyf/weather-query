package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.model.UserOperationLog;
import com.wilson.weatherquery.service.LogProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest; // 注意: SpringBoot 3 使用 jakarta
// 如果是 SpringBoot 2.x 使用 javax.servlet.http.HttpServletRequest
// 你的 parent version 是 3.5.7 (未来版本?)，假设是 3.x

@RestController
public class LogController {

    @Autowired
    private LogProducerService logProducerService;

    @PostMapping("/log/add")
    public String addLog(@RequestParam String userId, 
                         @RequestParam String operation,
                         HttpServletRequest request) {
        
        UserOperationLog log = new UserOperationLog(
                userId,
                operation,
                request.getRemoteAddr(),
                System.currentTimeMillis()
        );

        // 异步发送
        logProducerService.sendLog(log);

        return "Log added to queue successfully!";
    }
}