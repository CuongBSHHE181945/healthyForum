package com.healthyForum.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class IndexController {
    @GetMapping
    public String index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated (not anonymous)
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return "homePage"; // User is authenticated, show homepage
        } else {
            return "redirect:/login"; // User is not authenticated, redirect to login
        }
    }
}
