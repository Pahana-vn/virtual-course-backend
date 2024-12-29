package com.mytech.virtualcourse.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {
    @GetMapping("/api/protected")
    public String protectedEndpoint() {
        return "This is a protected endpoint!";
    }
}
