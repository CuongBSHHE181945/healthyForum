package com.healthyForum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "userAuthentication/login"; // maps to login.html
    }

    @GetMapping("/home")
    public String home() {
        return "homePage"; // maps to home.html
    }
}