package com.healthyForum.controller.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "suspended", required = false) String suspended,
                        @RequestParam(value = "logout", required = false) String logout,
                        @ModelAttribute("registerSuccessMsg") String registerSuccessMsg,
                        Model model) {

        if (registerSuccessMsg != null && !registerSuccessMsg.isEmpty()) {
            model.addAttribute("successMessage", registerSuccessMsg);
        }

        if (error != null) {
            if ("invalid_role".equals(error)) {
                model.addAttribute("errorMessage", "Role không hợp lệ. Vui lòng liên hệ admin.");
            } else {
                model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng.");
            }
        }

        if (suspended != null) {
            model.addAttribute("errorMessage", "Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ admin để được hỗ trợ.");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất thành công.");
        }

        return "userAuthentication/login"; // maps to login.html
    }

    @GetMapping("/home")
    public String home() {
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