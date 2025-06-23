package com.healthyForum.controller.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.model.User;
import com.healthyForum.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "userAuthentication/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
            userRepository.save(user);

            String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
            String resetLink = siteURL + "/reset-password?token=" + token;
            try {
                emailService.sendPasswordResetEmail(user, resetLink);
            } catch (MessagingException | UnsupportedEncodingException e) {
                model.addAttribute("message", "Error sending reset email. Please try again later.");
                return "userAuthentication/forgot-password";
            }
        }
        model.addAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        return "userAuthentication/forgot-password";
    }
} 