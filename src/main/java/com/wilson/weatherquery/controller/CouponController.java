package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/coupon/init")
    public String init() {
        couponService.initStock();
        return "Stock initialized to 5";
    }

    @GetMapping("/coupon/rush")
    public String rush(@RequestParam String userId, @RequestParam(defaultValue = "false") boolean safe) {
        if (safe) {
            return couponService.rushCouponSafe(userId);
        } else {
            return couponService.rushCouponUnsafe(userId);
        }
    }
}