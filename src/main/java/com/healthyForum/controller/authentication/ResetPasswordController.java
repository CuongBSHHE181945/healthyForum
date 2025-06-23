package com.healthyForum.controller.authentication;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ResetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);
        if (userOpt.isEmpty() || userOpt.get().getResetTokenExpiry() == null || userOpt.get().getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "userAuthentication/reset-password";
        }
        model.addAttribute("token", token);
        return "userAuthentication/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);
        if (userOpt.isEmpty() || userOpt.get().getResetTokenExpiry() == null || userOpt.get().getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "userAuthentication/reset-password";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "userAuthentication/reset-password";
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("verificationSuccess", "Your password has been reset successfully. You can now log in.");
        return "redirect:/login";
    }
} 