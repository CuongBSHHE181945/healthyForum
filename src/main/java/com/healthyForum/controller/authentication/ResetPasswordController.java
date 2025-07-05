package com.healthyForum.controller.authentication;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ResetPasswordController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "userAuthentication/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Validate password
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/reset-password?token=" + token;
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long.");
                return "redirect:/reset-password?token=" + token;
            }

            // Find account by reset token
            UserAccount account = userAccountService.findByResetPasswordToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

            // Check if token is expired
            if (account.getResetTokenExpiry() != null && 
                account.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Reset token has expired.");
                return "redirect:/forgot-password";
            }

            // Update password
            userAccountService.updatePassword(account, password);
            
            // Clear reset token
            userAccountService.clearResetPasswordToken(account);

            redirectAttributes.addFlashAttribute("success", "Password has been reset successfully.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/reset-password?token=" + token;
        }
    }
} 