package com.healthyForum.controller.profile;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChangePasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, @AuthenticationPrincipal Object principal) {
        User user = null;
        if (principal instanceof UserDetails userDetails) {
            user = userRepository.findByUsername(userDetails.getUsername())
                    .or(() -> userRepository.findByGoogleId(userDetails.getUsername()))
                    .orElse(null);
        }
        if (user == null) {
            // This will catch Google users (OAuth2User) and any other principal types
            return "redirect:/profile?error=google_user";
        }
        boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();
        model.addAttribute("hasPassword", hasPassword);
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam(required = false) String oldPassword,
                                        @RequestParam String newPassword,
                                        @RequestParam String confirmPassword,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .or(() -> userRepository.findByGoogleId(userDetails.getUsername()))
                .orElseThrow();
        boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();

        // If user has a password, require old password
        if (hasPassword) {
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Incorrect old password.");
                return "redirect:/change-password";
            }
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/change-password";
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", hasPassword ? "Password changed successfully." : "Password set successfully. You can now log in with your password.");
        return "redirect:/profile";
    }
} 