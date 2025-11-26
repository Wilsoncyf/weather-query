package com.wilson.weatherquery.controller;

import com.wilson.weatherquery.service.BigKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BigKeyController {

    @Autowired
    private BigKeyService bigKeyService;

    @GetMapping("/bigkey/create")
    public String create() {
        return bigKeyService.createBigKey();
    }

    @GetMapping("/bigkey/read")
    public String read() {
        return bigKeyService.readBigKey();
    }
    
    @GetMapping("/bigkey/delete")
    public String delete() {
        return bigKeyService.deleteBigKey();
    }
}