package com.vivekk.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "My Controller")
public class TestController {

    @GetMapping("/simple")
    @Operation(summary = "Get something")
    public String simpleEndpoint() {
        return "Works!";
    }
}