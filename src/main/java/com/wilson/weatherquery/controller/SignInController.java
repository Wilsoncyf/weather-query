package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.SignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInController {

    @Autowired
    private SignInService signInService;

    @GetMapping("/signIn")
    public String signIn(@RequestParam String userId) {
        return signInService.signIn(userId);
    }

    @GetMapping("/checkSignIn")
    public String checkSignIn(@RequestParam String userId) {
        return signInService.checkSignIn(userId);
    }

    @GetMapping("/getSignInCount")
    public long getSignInCount() {
        return signInService.getSignInCount();
    }
}
