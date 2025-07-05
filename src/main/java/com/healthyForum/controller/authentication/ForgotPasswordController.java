package com.healthyForum.controller.authentication;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.EmailService;
import com.healthyForum.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "userAuthentication/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                        RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserAccount account = userAccountRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("User account not found"));

            // Generate reset token
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(24);
            
            userAccountService.setResetPasswordToken(account, token, expiry);

            // Send email
            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user, resetLink);

            redirectAttributes.addFlashAttribute("success", 
                "Password reset link has been sent to your email.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "An error occurred. Please try again.");
            return "redirect:/forgot-password";
        }
    }
} 