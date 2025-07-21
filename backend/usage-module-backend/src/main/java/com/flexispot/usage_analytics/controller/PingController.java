package com.flexispot.usage_analytics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "FlexiSpot Usage Analytics Backend is working!";
    }
}
